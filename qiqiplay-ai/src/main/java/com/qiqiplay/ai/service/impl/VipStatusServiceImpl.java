package com.qiqiplay.ai.service.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.ai.service.ISysUserVipService;
import com.qiqiplay.ai.service.ISysUserDailyUsageService;
import com.qiqiplay.ai.service.IVipExpirationService;
import com.qiqiplay.ai.service.IVipStatusService;

/**
 * VIP状态实时管理服务实现类
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
@Service
public class VipStatusServiceImpl implements IVipStatusService
{
    private static final Logger log = LoggerFactory.getLogger(VipStatusServiceImpl.class);

    @Autowired
    private ISysUserVipService sysUserVipService;

    @Autowired
    private ISysUserDailyUsageService sysUserDailyUsageService;

    @Autowired
    private IVipExpirationService vipExpirationService;

    // 简单的内存缓存（实际项目中建议使用Redis）
    private final Map<Long, Map<String, Object>> vipStatusCache = new ConcurrentHashMap<>();
    private final Map<Long, Long> cacheTimestamp = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟缓存

    @Override
    public Map<String, Object> getRealTimeVipStatus(Long userId)
    {
        try
        {
            // 检查缓存
            if (isCacheValid(userId))
            {
                return vipStatusCache.get(userId);
            }

            // 获取最新状态
            Map<String, Object> vipStatus = buildVipStatusInfo(userId);

            // 缓存结果
            cacheUserVipStatus(userId, vipStatus);

            return vipStatus;
        }
        catch (Exception e)
        {
            log.error("获取实时VIP状态失败: userId={}", userId, e);
            return getDefaultVipStatus();
        }
    }

    @Override
    public Map<String, Object> checkAndRefreshVipStatus(Long userId)
    {
        try
        {
            // 先检查并更新过期状态
            vipExpirationService.checkAndUpdateUserVipStatus(userId);

            // 清除缓存，强制重新获取
            clearUserVipCache(userId);

            // 获取最新状态
            return getRealTimeVipStatus(userId);
        }
        catch (Exception e)
        {
            log.error("检查并刷新VIP状态失败: userId={}", userId, e);
            return getDefaultVipStatus();
        }
    }

    @Override
    public Map<String, Object> validateFeaturePermission(Long userId, String feature)
    {
        Map<String, Object> result = new HashMap<>();

        try
        {
            Map<String, Object> vipStatus = checkAndRefreshVipStatus(userId);
            Integer vipLevel = (Integer) vipStatus.get("vipLevel");
            Boolean isValid = (Boolean) vipStatus.get("isValid");

            if (!isValid || vipLevel == null)
            {
                result.put("hasPermission", false);
                result.put("message", "VIP已过期或无效");
                result.put("requiredLevel", getRequiredLevelForFeature(feature));
                return result;
            }

            Map<String, Object> privileges = getVipPrivileges(vipLevel);
            boolean hasPermission = false;
            String message = "";

            switch (feature.toLowerCase())
            {
                case "chat":
                    hasPermission = true; // 所有用户都可以聊天
                    message = "聊天功能可用";
                    break;
                case "voice":
                    hasPermission = (Boolean) privileges.getOrDefault("voiceEnabled", false);
                    message = hasPermission ? "语音功能可用" : "需要白银会员或更高等级";
                    break;
                case "image":
                    hasPermission = (Boolean) privileges.getOrDefault("imageEnabled", false);
                    message = hasPermission ? "图片生成功能可用" : "需要黄金会员或更高等级";
                    break;
                default:
                    hasPermission = false;
                    message = "未知功能";
            }

            result.put("hasPermission", hasPermission);
            result.put("message", message);
            result.put("vipLevel", vipLevel);
            result.put("vipLevelName", getVipLevelName(vipLevel));

            if (!hasPermission)
            {
                result.put("requiredLevel", getRequiredLevelForFeature(feature));
            }
        }
        catch (Exception e)
        {
            log.error("验证功能权限失败: userId={}, feature={}", userId, feature, e);
            result.put("hasPermission", false);
            result.put("message", "权限验证失败");
        }

        return result;
    }

    @Override
    public Map<String, Object> getTodayRemainingUsage(Long userId)
    {
        Map<String, Object> remaining = new HashMap<>();

        try
        {
            Map<String, Object> vipStatus = getRealTimeVipStatus(userId);
            Integer vipLevel = (Integer) vipStatus.get("vipLevel");
            Boolean isValid = (Boolean) vipStatus.get("isValid");

            if (!isValid || vipLevel == null)
            {
                // VIP无效，返回最低权限
                remaining.put("chatRemaining", 10); // 普通用户每日10次
                remaining.put("voiceRemaining", 0);
                remaining.put("imageRemaining", 0);
                return remaining;
            }

            Map<String, Object> privileges = getVipPrivileges(vipLevel);

            // 聊天剩余次数
            Integer dailyChatLimit = (Integer) privileges.get("dailyChatLimit");
            int chatUsed = sysUserDailyUsageService.getTodayChatUsage(userId);
            remaining.put("chatRemaining", dailyChatLimit == -1 ? -1 : Math.max(0, dailyChatLimit - chatUsed));

            // 语音剩余次数
            Integer dailyVoiceLimit = (Integer) privileges.get("dailyVoiceLimit");
            int voiceUsed = sysUserDailyUsageService.getTodayVoiceUsage(userId);
            remaining.put("voiceRemaining", dailyVoiceLimit == -1 ? -1 : Math.max(0, dailyVoiceLimit - voiceUsed));

            // 图片剩余次数
            Integer dailyImageLimit = (Integer) privileges.get("dailyImageLimit");
            int imageUsed = sysUserDailyUsageService.getTodayImageUsage(userId);
            remaining.put("imageRemaining", dailyImageLimit == -1 ? -1 : Math.max(0, dailyImageLimit - imageUsed));

            // 添加限制信息
            remaining.put("chatLimit", dailyChatLimit);
            remaining.put("voiceLimit", dailyVoiceLimit);
            remaining.put("imageLimit", dailyImageLimit);

        }
        catch (Exception e)
        {
            log.error("获取今日剩余使用次数失败: userId={}", userId, e);
            // 返回保守估计
            remaining.put("chatRemaining", 0);
            remaining.put("voiceRemaining", 0);
            remaining.put("imageRemaining", 0);
        }

        return remaining;
    }

    @Override
    public boolean canUseFeature(Long userId, String feature)
    {
        try
        {
            Map<String, Object> permission = validateFeaturePermission(userId, feature);
            return (Boolean) permission.getOrDefault("hasPermission", false);
        }
        catch (Exception e)
        {
            log.error("检查功能使用权限失败: userId={}, feature={}", userId, feature, e);
            return false;
        }
    }

    @Override
    public String getVipLevelName(Integer level)
    {
        if (level == null || level <= 0)
        {
            return "普通用户";
        }

        switch (level)
        {
            case 1: return "白银会员";
            case 2: return "黄金会员";
            case 3: return "铂金会员";
            case 4: return "钻石会员";
            default: return "未知等级";
        }
    }

    @Override
    public Map<String, Object> getVipPrivileges(Integer level)
    {
        Map<String, Object> privileges = new HashMap<>();

        switch (level == null ? 0 : level)
        {
            case 0: // 普通用户
                privileges.put("dailyChatLimit", 10);
                privileges.put("dailyVoiceLimit", 0);
                privileges.put("dailyImageLimit", 0);
                privileges.put("voiceEnabled", false);
                privileges.put("imageEnabled", false);
                privileges.put("customRoleQuota", 0);
                break;
            case 1: // 白银会员
                privileges.put("dailyChatLimit", 50);
                privileges.put("dailyVoiceLimit", 20);
                privileges.put("dailyImageLimit", 0);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", false);
                privileges.put("customRoleQuota", 2);
                break;
            case 2: // 黄金会员
                privileges.put("dailyChatLimit", 150);
                privileges.put("dailyVoiceLimit", 50);
                privileges.put("dailyImageLimit", 10);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("customRoleQuota", 5);
                break;
            case 3: // 铂金会员
                privileges.put("dailyChatLimit", 500);
                privileges.put("dailyVoiceLimit", 150);
                privileges.put("dailyImageLimit", 30);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("customRoleQuota", 10);
                break;
            case 4: // 钻石会员
                privileges.put("dailyChatLimit", -1); // 无限
                privileges.put("dailyVoiceLimit", -1); // 无限
                privileges.put("dailyImageLimit", -1); // 无限
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("customRoleQuota", -1); // 无限
                break;
        }

        return privileges;
    }

    @Override
    public void cacheUserVipStatus(Long userId, Map<String, Object> vipInfo)
    {
        vipStatusCache.put(userId, new HashMap<>(vipInfo));
        cacheTimestamp.put(userId, System.currentTimeMillis());
    }

    @Override
    public void clearUserVipCache(Long userId)
    {
        vipStatusCache.remove(userId);
        cacheTimestamp.remove(userId);
    }

    /**
     * 构建VIP状态信息
     */
    private Map<String, Object> buildVipStatusInfo(Long userId)
    {
        Map<String, Object> status = new HashMap<>();

        try
        {
            SysUserVip userVip = sysUserVipService.selectValidVipByUserId(userId);

            if (userVip != null && "1".equals(userVip.getVipStatus()))
            {
                Date now = new Date();
                boolean isValid = userVip.getExpireTime() == null || userVip.getExpireTime().after(now);

                status.put("hasVip", true);
                status.put("isValid", isValid);
                status.put("vipLevel", userVip.getVipLevel());
                status.put("vipLevelName", getVipLevelName(userVip.getVipLevel()));
                status.put("expireTime", userVip.getExpireTime());
                status.put("subscribeTime", userVip.getSubscribeTime());

                if (userVip.getExpireTime() != null)
                {
                    long diffInMillies = userVip.getExpireTime().getTime() - now.getTime();
                    int daysLeft = (int) (diffInMillies / (1000 * 60 * 60 * 24));
                    status.put("daysLeft", Math.max(0, daysLeft));
                }

                // 添加权益信息
                status.put("privileges", getVipPrivileges(userVip.getVipLevel()));
            }
            else
            {
                // 普通用户
                status.put("hasVip", false);
                status.put("isValid", true);
                status.put("vipLevel", 0);
                status.put("vipLevelName", "普通用户");
                status.put("privileges", getVipPrivileges(0));
            }

            status.put("userId", userId);
            status.put("updateTime", System.currentTimeMillis());
        }
        catch (Exception e)
        {
            log.error("构建VIP状态信息失败: userId={}", userId, e);
            return getDefaultVipStatus();
        }

        return status;
    }

    /**
     * 检查缓存是否有效
     */
    private boolean isCacheValid(Long userId)
    {
        if (!vipStatusCache.containsKey(userId) || !cacheTimestamp.containsKey(userId))
        {
            return false;
        }

        long cachedTime = cacheTimestamp.get(userId);
        return (System.currentTimeMillis() - cachedTime) < CACHE_EXPIRE_TIME;
    }

    /**
     * 获取默认VIP状态
     */
    private Map<String, Object> getDefaultVipStatus()
    {
        Map<String, Object> status = new HashMap<>();
        status.put("hasVip", false);
        status.put("isValid", true);
        status.put("vipLevel", 0);
        status.put("vipLevelName", "普通用户");
        status.put("privileges", getVipPrivileges(0));
        return status;
    }

    /**
     * 获取功能所需的最低VIP等级
     */
    private int getRequiredLevelForFeature(String feature)
    {
        switch (feature.toLowerCase())
        {
            case "voice": return 1; // 白银会员
            case "image": return 2; // 黄金会员
            default: return 0; // 普通用户
        }
    }
}