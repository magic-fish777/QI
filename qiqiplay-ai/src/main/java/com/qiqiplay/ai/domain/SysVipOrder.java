package com.qiqiplay.ai.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * VIP订单对象 sys_vip_order
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
public class SysVipOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** VIP等级 */
    @Excel(name = "VIP等级")
    private Integer vipLevel;

    /** 购买类型（1-月卡，2-季卡，3-年卡） */
    @Excel(name = "购买类型", dictType = "vip_period_type")
    private String periodType;

    /** 购买时长（月） */
    @Excel(name = "购买时长")
    private Integer periodMonths;

    /** 订单金额（元） */
    @Excel(name = "订单金额")
    private BigDecimal orderAmount;

    /** 支付方式（1-支付宝，2-微信支付） */
    @Excel(name = "支付方式", dictType = "payment_method")
    private String paymentMethod;

    /** 订单状态（0-待支付，1-已支付，2-已取消，3-已退款） */
    @Excel(name = "订单状态", dictType = "order_status")
    private String orderStatus;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 第三方交易号 */
    @Excel(name = "第三方交易号")
    private String transactionId;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }
    public void setVipLevel(Integer vipLevel)
    {
        this.vipLevel = vipLevel;
    }

    public Integer getVipLevel()
    {
        return vipLevel;
    }
    public void setPeriodType(String periodType)
    {
        this.periodType = periodType;
    }

    public String getPeriodType()
    {
        return periodType;
    }
    public void setPeriodMonths(Integer periodMonths)
    {
        this.periodMonths = periodMonths;
    }

    public Integer getPeriodMonths()
    {
        return periodMonths;
    }
    public void setOrderAmount(BigDecimal orderAmount)
    {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderAmount()
    {
        return orderAmount;
    }
    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }
    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Date getPayTime()
    {
        return payTime;
    }
    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getTransactionId()
    {
        return transactionId;
    }
    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("userId", getUserId())
            .append("orderNo", getOrderNo())
            .append("vipLevel", getVipLevel())
            .append("periodType", getPeriodType())
            .append("periodMonths", getPeriodMonths())
            .append("orderAmount", getOrderAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("orderStatus", getOrderStatus())
            .append("payTime", getPayTime())
            .append("transactionId", getTransactionId())
            .append("expireTime", getExpireTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}