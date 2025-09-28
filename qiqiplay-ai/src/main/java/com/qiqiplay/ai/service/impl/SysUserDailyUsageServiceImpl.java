package com.qiqiplay.ai.service.impl;

import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysUserDailyUsageMapper;
import com.qiqiplay.ai.domain.SysUserDailyUsage;
import com.qiqiplay.ai.service.ISysUserDailyUsageService;

/**
 * 用户每日使用统计Service业务层处理
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
@Service
public class SysUserDailyUsageServiceImpl implements ISysUserDailyUsageService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserDailyUsageServiceImpl.class);

    @Autowired
    private SysUserDailyUsageMapper sysUserDailyUsageMapper;

    /**
     * 查询用户每日使用统计
     *
     * @param usageId 用户每日使用统计主键
     * @return 用户每日使用统计
     */
    @Override
    public SysUserDailyUsage selectSysUserDailyUsageByUsageId(Long usageId)
    {
        return sysUserDailyUsageMapper.selectSysUserDailyUsageByUsageId(usageId);
    }

    /**
     * 查询用户每日使用统计列表
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 用户每日使用统计
     */
    @Override
    public List<SysUserDailyUsage> selectSysUserDailyUsageList(SysUserDailyUsage sysUserDailyUsage)
    {
        return sysUserDailyUsageMapper.selectSysUserDailyUsageList(sysUserDailyUsage);
    }

    /**
     * 新增用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    @Override
    public int insertSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage)
    {
        return sysUserDailyUsageMapper.insertSysUserDailyUsage(sysUserDailyUsage);
    }

    /**
     * 修改用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    @Override
    public int updateSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage)
    {
        return sysUserDailyUsageMapper.updateSysUserDailyUsage(sysUserDailyUsage);
    }

    /**
     * 批量删除用户每日使用统计
     *
     * @param usageIds 需要删除的用户每日使用统计主键
     * @return 结果
     */
    @Override
    public int deleteSysUserDailyUsageByUsageIds(Long[] usageIds)
    {
        return sysUserDailyUsageMapper.deleteSysUserDailyUsageByUsageIds(usageIds);
    }

    /**
     * 删除用户每日使用统计信息
     *
     * @param usageId 用户每日使用统计主键
     * @return 结果
     */
    @Override
    public int deleteSysUserDailyUsageByUsageId(Long usageId)
    {
        return sysUserDailyUsageMapper.deleteSysUserDailyUsageByUsageId(usageId);
    }

    /**
     * 根据用户ID和日期查询使用统计
     *
     * @param userId 用户ID
     * @param date 日期
     * @return 使用统计
     */
    @Override
    public SysUserDailyUsage getTodayUsage(Long userId, Date date)
    {
        try {
            return sysUserDailyUsageMapper.selectByUserIdAndDate(userId, date);
        } catch (Exception e) {
            log.error("查询用户每日使用统计失败: userId={}, date={}, error={}", userId, date, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 记录聊天使用次数
     *
     * @param userId 用户ID
     * @param isVoice 是否语音聊天
     */
    @Override
    public void recordChatUsage(Long userId, boolean isVoice)
    {
        if (userId == null) {
            log.warn("用户ID为空，无法记录聊天使用次数");
            return;
        }

        try {
            Date today = getTodayDate();

            // 记录聊天次数
            sysUserDailyUsageMapper.incrementChatCount(userId, today);

            // 如果是语音聊天，同时记录语音使用次数
            if (isVoice) {
                sysUserDailyUsageMapper.incrementVoiceCount(userId, today);
            }

            log.info("记录聊天使用次数成功: userId={}, isVoice={}", userId, isVoice);
        } catch (Exception e) {
            log.error("记录聊天使用次数失败: userId={}, isVoice={}, error={}", userId, isVoice, e.getMessage(), e);
        }
    }

    /**
     * 记录图片生成使用次数
     *
     * @param userId 用户ID
     */
    @Override
    public void recordImageUsage(Long userId)
    {
        if (userId == null) {
            log.warn("用户ID为空，无法记录图片生成使用次数");
            return;
        }

        try {
            Date today = getTodayDate();
            sysUserDailyUsageMapper.incrementImageCount(userId, today);
            log.info("记录图片生成使用次数成功: userId={}", userId);
        } catch (Exception e) {
            log.error("记录图片生成使用次数失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否达到每日聊天限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    @Override
    public boolean hasReachedDailyChatLimit(Long userId, int dailyLimit)
    {
        if (dailyLimit == -1) {
            // 无限制
            return false;
        }

        if (userId == null || dailyLimit <= 0) {
            return true; // 安全起见，返回已达到限制
        }

        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                // 今日还没有使用记录，未达到限制
                return false;
            }

            int todayChatCount = usage.getChatCount() != null ? usage.getChatCount() : 0;
            boolean hasReached = todayChatCount >= dailyLimit;

            log.info("检查聊天限制: userId={}, 今日使用={}, 每日限制={}, 是否达到限制={}",
                    userId, todayChatCount, dailyLimit, hasReached);

            return hasReached;
        } catch (Exception e) {
            log.error("检查聊天限制失败: userId={}, dailyLimit={}, error={}", userId, dailyLimit, e.getMessage(), e);
            return true; // 异常时安全起见，返回已达到限制
        }
    }

    /**
     * 检查用户是否达到每日图片生成限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    @Override
    public boolean hasReachedDailyImageLimit(Long userId, int dailyLimit)
    {
        if (dailyLimit == -1) {
            // 无限制
            return false;
        }

        if (userId == null || dailyLimit <= 0) {
            return true; // 安全起见，返回已达到限制
        }

        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                // 今日还没有使用记录，未达到限制
                return false;
            }

            int todayImageCount = usage.getImageCount() != null ? usage.getImageCount() : 0;
            boolean hasReached = todayImageCount >= dailyLimit;

            log.info("检查图片生成限制: userId={}, 今日使用={}, 每日限制={}, 是否达到限制={}",
                    userId, todayImageCount, dailyLimit, hasReached);

            return hasReached;
        } catch (Exception e) {
            log.error("检查图片生成限制失败: userId={}, dailyLimit={}, error={}", userId, dailyLimit, e.getMessage(), e);
            return true; // 异常时安全起见，返回已达到限制
        }
    }

    /**
     * 获取用户今日聊天使用次数
     *
     * @param userId 用户ID
     * @return 今日聊天次数
     */
    @Override
    public int getTodayChatUsage(Long userId)
    {
        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                return 0;
            }

            return usage.getChatCount() != null ? usage.getChatCount() : 0;
        } catch (Exception e) {
            log.error("获取今日聊天使用次数失败: userId={}, error={}", userId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取用户今日语音使用次数
     *
     * @param userId 用户ID
     * @return 今日语音次数
     */
    @Override
    public int getTodayVoiceUsage(Long userId)
    {
        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                return 0;
            }

            return usage.getVoiceCount() != null ? usage.getVoiceCount() : 0;
        } catch (Exception e) {
            log.error("获取今日语音使用次数失败: userId={}, error={}", userId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取用户今日图片生成使用次数
     *
     * @param userId 用户ID
     * @return 今日图片生成次数
     */
    @Override
    public int getTodayImageUsage(Long userId)
    {
        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                return 0;
            }

            return usage.getImageCount() != null ? usage.getImageCount() : 0;
        } catch (Exception e) {
            log.error("获取今日图片生成使用次数失败: userId={}, error={}", userId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 记录语音使用次数
     *
     * @param userId 用户ID
     */
    @Override
    public void recordVoiceUsage(Long userId)
    {
        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                // 创建新的使用记录
                usage = new SysUserDailyUsage();
                usage.setUserId(userId);
                usage.setUsageDate(today);
                usage.setChatCount(0);
                usage.setVoiceCount(1);
                usage.setImageCount(0);
                usage.setCreateTime(new Date());

                sysUserDailyUsageMapper.insertSysUserDailyUsage(usage);
                log.info("创建语音使用记录: userId={}, voiceCount=1", userId);
            } else {
                // 更新现有记录
                int currentVoiceCount = usage.getVoiceCount() != null ? usage.getVoiceCount() : 0;
                usage.setVoiceCount(currentVoiceCount + 1);
                usage.setUpdateTime(new Date());

                sysUserDailyUsageMapper.updateSysUserDailyUsage(usage);
                log.info("更新语音使用记录: userId={}, voiceCount={}", userId, usage.getVoiceCount());
            }
        } catch (Exception e) {
            log.error("记录语音使用次数失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否达到每日语音限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    @Override
    public boolean hasReachedDailyVoiceLimit(Long userId, int dailyLimit)
    {
        if (dailyLimit == -1) {
            // 无限制
            return false;
        }

        if (userId == null || dailyLimit <= 0) {
            return true; // 安全起见，返回已达到限制
        }

        try {
            Date today = getTodayDate();
            SysUserDailyUsage usage = getTodayUsage(userId, today);

            if (usage == null) {
                // 今日还没有使用记录，未达到限制
                return false;
            }

            int todayVoiceCount = usage.getVoiceCount() != null ? usage.getVoiceCount() : 0;
            boolean hasReached = todayVoiceCount >= dailyLimit;

            log.info("检查语音限制: userId={}, 今日使用={}, 每日限制={}, 是否达到限制={}",
                    userId, todayVoiceCount, dailyLimit, hasReached);

            return hasReached;
        } catch (Exception e) {
            log.error("检查语音限制失败: userId={}, dailyLimit={}, error={}", userId, dailyLimit, e.getMessage(), e);
            return true; // 异常时安全起见，返回已达到限制
        }
    }

    /**
     * 获取今天的日期（只有年月日，时分秒为0）
     *
     * @return 今天的日期
     */
    private Date getTodayDate() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}