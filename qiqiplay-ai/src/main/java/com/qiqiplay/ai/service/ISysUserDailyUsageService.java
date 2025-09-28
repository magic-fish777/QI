package com.qiqiplay.ai.service;

import java.util.List;
import java.util.Date;
import com.qiqiplay.ai.domain.SysUserDailyUsage;

/**
 * 用户每日使用统计Service接口
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
public interface ISysUserDailyUsageService
{
    /**
     * 查询用户每日使用统计
     *
     * @param usageId 用户每日使用统计主键
     * @return 用户每日使用统计
     */
    public SysUserDailyUsage selectSysUserDailyUsageByUsageId(Long usageId);

    /**
     * 查询用户每日使用统计列表
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 用户每日使用统计集合
     */
    public List<SysUserDailyUsage> selectSysUserDailyUsageList(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 新增用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    public int insertSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 修改用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    public int updateSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 批量删除用户每日使用统计
     *
     * @param usageIds 需要删除的用户每日使用统计主键集合
     * @return 结果
     */
    public int deleteSysUserDailyUsageByUsageIds(Long[] usageIds);

    /**
     * 删除用户每日使用统计信息
     *
     * @param usageId 用户每日使用统计主键
     * @return 结果
     */
    public int deleteSysUserDailyUsageByUsageId(Long usageId);

    /**
     * 根据用户ID和日期查询使用统计
     *
     * @param userId 用户ID
     * @param date 日期
     * @return 使用统计
     */
    public SysUserDailyUsage getTodayUsage(Long userId, Date date);

    /**
     * 记录聊天使用次数
     *
     * @param userId 用户ID
     * @param isVoice 是否语音聊天
     */
    public void recordChatUsage(Long userId, boolean isVoice);

    /**
     * 记录图片生成使用次数
     *
     * @param userId 用户ID
     */
    public void recordImageUsage(Long userId);

    /**
     * 检查用户是否达到每日聊天限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    public boolean hasReachedDailyChatLimit(Long userId, int dailyLimit);

    /**
     * 检查用户是否达到每日图片生成限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    public boolean hasReachedDailyImageLimit(Long userId, int dailyLimit);

    /**
     * 获取用户今日聊天使用次数
     *
     * @param userId 用户ID
     * @return 今日聊天次数
     */
    public int getTodayChatUsage(Long userId);

    /**
     * 获取用户今日语音使用次数
     *
     * @param userId 用户ID
     * @return 今日语音次数
     */
    public int getTodayVoiceUsage(Long userId);

    /**
     * 获取用户今日图片生成使用次数
     *
     * @param userId 用户ID
     * @return 今日图片生成次数
     */
    public int getTodayImageUsage(Long userId);

    /**
     * 记录语音使用次数
     *
     * @param userId 用户ID
     */
    public void recordVoiceUsage(Long userId);

    /**
     * 检查用户是否达到每日语音限制
     *
     * @param userId 用户ID
     * @param dailyLimit 每日限制（-1为无限制）
     * @return 是否达到限制
     */
    public boolean hasReachedDailyVoiceLimit(Long userId, int dailyLimit);
}