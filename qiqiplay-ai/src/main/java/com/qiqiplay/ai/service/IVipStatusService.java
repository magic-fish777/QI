package com.qiqiplay.ai.service;

import java.util.Map;

/**
 * VIP状态实时管理服务接口
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
public interface IVipStatusService
{
    /**
     * 获取用户实时VIP状态信息
     * @param userId 用户ID
     * @return VIP状态信息
     */
    Map<String, Object> getRealTimeVipStatus(Long userId);

    /**
     * 检查用户VIP状态并自动更新（如果过期）
     * @param userId 用户ID
     * @return 最新的VIP状态信息
     */
    Map<String, Object> checkAndRefreshVipStatus(Long userId);

    /**
     * 验证用户是否有指定功能的权限
     * @param userId 用户ID
     * @param feature 功能名称（chat, image, voice等）
     * @return 权限验证结果
     */
    Map<String, Object> validateFeaturePermission(Long userId, String feature);

    /**
     * 获取用户今日剩余使用次数
     * @param userId 用户ID
     * @return 各功能剩余次数
     */
    Map<String, Object> getTodayRemainingUsage(Long userId);

    /**
     * 检查是否可以使用某个功能
     * @param userId 用户ID
     * @param feature 功能名称
     * @return 是否可以使用
     */
    boolean canUseFeature(Long userId, String feature);

    /**
     * 获取VIP等级名称
     * @param level VIP等级
     * @return 等级名称
     */
    String getVipLevelName(Integer level);

    /**
     * 获取VIP等级对应的权益信息
     * @param level VIP等级
     * @return 权益信息
     */
    Map<String, Object> getVipPrivileges(Integer level);

    /**
     * 缓存用户VIP状态（提高性能）
     * @param userId 用户ID
     * @param vipInfo VIP信息
     */
    void cacheUserVipStatus(Long userId, Map<String, Object> vipInfo);

    /**
     * 清除用户VIP状态缓存
     * @param userId 用户ID
     */
    void clearUserVipCache(Long userId);
}