package com.qiqiplay.ai.service;

import com.qiqiplay.ai.domain.SysVipOrder;
import java.util.Map;

/**
 * VIP支付服务接口
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
public interface IVipPaymentService
{
    /**
     * 创建VIP购买订单
     *
     * @param userId 用户ID
     * @param vipLevel VIP等级
     * @param periodType 购买类型（monthly, quarterly, yearly）
     * @param paymentMethod 支付方式（alipay, wechat）
     * @return 订单信息
     */
    public SysVipOrder createVipOrder(Long userId, Integer vipLevel, String periodType, String paymentMethod);

    /**
     * 处理支付成功回调
     *
     * @param orderNo 订单号
     * @param transactionId 第三方交易号
     * @param paymentMethod 支付方式
     * @return 处理结果
     */
    public boolean handlePaymentSuccess(String orderNo, String transactionId, String paymentMethod);

    /**
     * 开通或续费用户VIP
     *
     * @param order VIP订单
     * @return 处理结果
     */
    public boolean activateUserVip(SysVipOrder order);

    /**
     * 获取支付参数（用于调起支付）
     *
     * @param order 订单信息
     * @return 支付参数
     */
    public Map<String, Object> getPaymentParams(SysVipOrder order);

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 处理结果
     */
    public boolean cancelOrder(String orderNo, Long userId);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    public SysVipOrder getOrderByOrderNo(String orderNo);

    /**
     * 根据VIP等级和购买类型获取价格
     *
     * @param vipLevel VIP等级
     * @param periodType 购买类型
     * @return 价格（分）
     */
    public Long getVipPrice(Integer vipLevel, String periodType);
}