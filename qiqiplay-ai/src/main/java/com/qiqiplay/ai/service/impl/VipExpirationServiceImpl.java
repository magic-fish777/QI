package com.qiqiplay.ai.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.ai.service.ISysUserVipService;
import com.qiqiplay.ai.service.IVipExpirationService;

/**
 * VIP过期处理服务实现类
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
@Service
public class VipExpirationServiceImpl implements IVipExpirationService
{
    private static final Logger log = LoggerFactory.getLogger(VipExpirationServiceImpl.class);

    @Autowired
    private ISysUserVipService sysUserVipService;

    @Override
    @Transactional
    public Map<String, Object> processExpiredVips()
    {
        Map<String, Object> result = new HashMap<>();
        int processedCount = 0;
        int errorCount = 0;

        try
        {
            log.info("开始处理过期VIP会员...");

            // 查询所有活跃的VIP会员
            SysUserVip queryVip = new SysUserVip();
            queryVip.setVipStatus("1"); // 只查询活跃状态
            List<SysUserVip> activeVips = sysUserVipService.selectSysUserVipList(queryVip);

            Date now = new Date();
            List<Long> expiredVipIds = new ArrayList<>();

            // 检查每个会员是否过期
            for (SysUserVip vip : activeVips)
            {
                if (vip.getExpireTime() != null && vip.getExpireTime().before(now))
                {
                    expiredVipIds.add(vip.getVipId());
                }
            }

            if (!expiredVipIds.isEmpty())
            {
                log.info("发现 {} 个过期VIP会员，开始批量更新状态", expiredVipIds.size());
                processedCount = batchUpdateExpiredStatus(expiredVipIds);
                log.info("成功处理 {} 个过期VIP会员", processedCount);
            }

            // 处理即将过期的会员（发送提醒）
            List<Map<String, Object>> expiringVips = getExpiringVips();
            for (Map<String, Object> vipInfo : expiringVips)
            {
                try
                {
                    Long userId = (Long) vipInfo.get("userId");
                    Integer daysLeft = (Integer) vipInfo.get("daysLeft");
                    sendExpirationNotice(userId, daysLeft);
                }
                catch (Exception e)
                {
                    log.error("发送过期提醒失败: {}", e.getMessage());
                    errorCount++;
                }
            }

            result.put("processedExpiredCount", processedCount);
            result.put("expiringNoticeCount", expiringVips.size());
            result.put("errorCount", errorCount);
            result.put("success", true);
            result.put("message", "VIP过期处理完成");

        }
        catch (Exception e)
        {
            log.error("处理过期VIP会员失败", e);
            result.put("success", false);
            result.put("message", "处理失败: " + e.getMessage());
            result.put("processedExpiredCount", processedCount);
            result.put("errorCount", errorCount);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getExpiringVips()
    {
        List<Map<String, Object>> expiringVips = new ArrayList<>();

        try
        {
            // 查询所有活跃的VIP会员
            SysUserVip queryVip = new SysUserVip();
            queryVip.setVipStatus("1");
            List<SysUserVip> activeVips = sysUserVipService.selectSysUserVipList(queryVip);

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 7); // 7天后的日期
            Date sevenDaysLater = calendar.getTime();

            for (SysUserVip vip : activeVips)
            {
                if (vip.getExpireTime() != null &&
                    vip.getExpireTime().after(now) && // 还未过期
                    vip.getExpireTime().before(sevenDaysLater)) // 但在7天内过期
                {
                    Map<String, Object> vipInfo = new HashMap<>();
                    vipInfo.put("vipId", vip.getVipId());
                    vipInfo.put("userId", vip.getUserId());
                    vipInfo.put("vipLevel", vip.getVipLevel());
                    vipInfo.put("expireTime", vip.getExpireTime());

                    // 计算剩余天数
                    long diffInMillies = vip.getExpireTime().getTime() - now.getTime();
                    int daysLeft = (int) (diffInMillies / (1000 * 60 * 60 * 24));
                    vipInfo.put("daysLeft", Math.max(0, daysLeft));

                    expiringVips.add(vipInfo);
                }
            }

            log.info("发现 {} 个即将过期的VIP会员", expiringVips.size());
        }
        catch (Exception e)
        {
            log.error("获取即将过期VIP会员失败", e);
        }

        return expiringVips;
    }

    @Override
    public void sendExpirationNotice(Long userId, int daysLeft)
    {
        try
        {
            // 这里实现发送通知的逻辑
            // 可以发送站内信、邮件、短信等
            log.info("发送VIP过期提醒通知: userId={}, daysLeft={}", userId, daysLeft);

            // TODO: 集成实际的通知服务
            // 1. 站内消息通知
            // 2. 邮件通知
            // 3. 短信通知
            // 4. 推送通知

            String noticeContent = String.format("您的VIP会员将在%d天后过期，请及时续费以继续享受会员特权", daysLeft);
            log.info("通知内容: {}", noticeContent);
        }
        catch (Exception e)
        {
            log.error("发送过期提醒通知失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public int batchUpdateExpiredStatus(List<Long> expiredVipIds)
    {
        int successCount = 0;

        try
        {
            for (Long vipId : expiredVipIds)
            {
                try
                {
                    SysUserVip vip = sysUserVipService.selectSysUserVipByVipId(vipId);
                    if (vip != null)
                    {
                        vip.setVipStatus("0"); // 设置为过期状态
                        vip.setUpdateTime(new Date());
                        vip.setRemark("系统自动处理：会员已过期");

                        sysUserVipService.updateSysUserVip(vip);
                        successCount++;

                        log.debug("更新过期VIP状态成功: vipId={}, userId={}", vipId, vip.getUserId());
                    }
                }
                catch (Exception e)
                {
                    log.error("更新VIP过期状态失败: vipId={}, error={}", vipId, e.getMessage());
                }
            }

            log.info("批量更新过期VIP状态完成: 成功={}, 总数={}", successCount, expiredVipIds.size());
        }
        catch (Exception e)
        {
            log.error("批量更新过期VIP状态异常", e);
        }

        return successCount;
    }

    @Override
    public boolean checkAndUpdateUserVipStatus(Long userId)
    {
        try
        {
            SysUserVip userVip = sysUserVipService.selectValidVipByUserId(userId);
            if (userVip != null)
            {
                Date now = new Date();

                // 检查是否过期
                if ("1".equals(userVip.getVipStatus()) &&
                    userVip.getExpireTime() != null &&
                    userVip.getExpireTime().before(now))
                {
                    // 更新为过期状态
                    userVip.setVipStatus("0");
                    userVip.setUpdateTime(now);
                    userVip.setRemark("系统检查：会员已过期");

                    sysUserVipService.updateSysUserVip(userVip);

                    log.info("用户VIP状态已更新为过期: userId={}, vipId={}", userId, userVip.getVipId());
                    return true;
                }
            }

            return false;
        }
        catch (Exception e)
        {
            log.error("检查并更新用户VIP状态失败: userId={}", userId, e);
            return false;
        }
    }
}