package com.qiqiplay.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 会员订阅对象 sys_user_vip
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysUserVip extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    private Long vipId;

    /** 用户ID（sys_user.user_id） */
    @Excel(name = "用户ID", readConverterExp = "s=ys_user.user_id")
    private Long userId;

    /** 会员类型（1月卡 2季卡 3年卡） */
    @Excel(name = "会员类型", readConverterExp = "1=月卡,2=季卡,3=年卡")
    private String vipType;

    /** 订阅时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订阅时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date subscribeTime;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

    /** 状态（0过期 1有效 2取消） */
    @Excel(name = "状态", readConverterExp = "0=过期,1=有效,2=取消")
    private String vipStatus;

    /** 定制角色配额 */
    @Excel(name = "定制角色配额")
    private Long customRoleQuota;

    public void setVipId(Long vipId) 
    {
        this.vipId = vipId;
    }

    public Long getVipId() 
    {
        return vipId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setVipType(String vipType) 
    {
        this.vipType = vipType;
    }

    public String getVipType() 
    {
        return vipType;
    }

    public void setSubscribeTime(Date subscribeTime) 
    {
        this.subscribeTime = subscribeTime;
    }

    public Date getSubscribeTime() 
    {
        return subscribeTime;
    }

    public void setExpireTime(Date expireTime) 
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime() 
    {
        return expireTime;
    }

    public void setVipStatus(String vipStatus) 
    {
        this.vipStatus = vipStatus;
    }

    public String getVipStatus() 
    {
        return vipStatus;
    }

    public void setCustomRoleQuota(Long customRoleQuota) 
    {
        this.customRoleQuota = customRoleQuota;
    }

    public Long getCustomRoleQuota() 
    {
        return customRoleQuota;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("vipId", getVipId())
            .append("userId", getUserId())
            .append("vipType", getVipType())
            .append("subscribeTime", getSubscribeTime())
            .append("expireTime", getExpireTime())
            .append("vipStatus", getVipStatus())
            .append("customRoleQuota", getCustomRoleQuota())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
