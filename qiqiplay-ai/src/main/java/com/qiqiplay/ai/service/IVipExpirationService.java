package com.qiqiplay.ai.service;

import java.util.List;
import java.util.Map;

/**
 * VIP过期处理服务接口
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
public interface IVipExpirationService
{
    /**
     * 检查并处理过期的VIP会员
     * @return 处理结果信息
     */
    Map<String, Object> processExpiredVips();

    /**
     * 获取即将过期的VIP会员列表（7天内）
     * @return 即将过期的会员列表
     */
    List<Map<String, Object>> getExpiringVips();

    /**
     * 发送过期提醒通知
     * @param userId 用户ID
     * @param daysLeft 剩余天数
     */
    void sendExpirationNotice(Long userId, int daysLeft);

    /**
     * 批量更新过期会员状态
     * @param expiredVipIds 过期会员ID列表
     * @return 更新成功数量
     */
    int batchUpdateExpiredStatus(List<Long> expiredVipIds);

    /**
     * 检查单个用户VIP状态并更新
     * @param userId 用户ID
     * @return 是否需要更新
     */
    boolean checkAndUpdateUserVipStatus(Long userId);
}