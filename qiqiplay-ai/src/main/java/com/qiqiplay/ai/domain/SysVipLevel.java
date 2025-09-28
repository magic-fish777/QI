package com.qiqiplay.ai.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 会员等级配置对象 sys_vip_level
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
public class SysVipLevel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 等级ID */
    private Long levelId;

    /** 等级编号（0普通 1白银 2黄金 3铂金 4钻石） */
    @Excel(name = "等级编号")
    private Integer levelCode;

    /** 等级名称 */
    @Excel(name = "等级名称")
    private String levelName;

    /** 等级显示名称 */
    @Excel(name = "等级显示名称")
    private String levelDisplayName;

    /** 每日对话次数限制（-1为无限） */
    @Excel(name = "每日对话次数限制")
    private Integer dailyChatLimit;

    /** 每日图片生成次数限制（-1为无限） */
    @Excel(name = "每日图片生成次数限制")
    private Integer dailyImageLimit;

    /** 定制角色配额（-1为无限） */
    @Excel(name = "定制角色配额")
    private Integer customRoleQuota;

    /** 是否支持语音功能（0否 1是） */
    @Excel(name = "是否支持语音功能", readConverterExp = "0=否,1=是")
    private String voiceEnabled;

    /** 是否支持图片生成（0否 1是） */
    @Excel(name = "是否支持图片生成", readConverterExp = "0=否,1=是")
    private String imageEnabled;

    /** 是否支持专属角色（0否 1是） */
    @Excel(name = "是否支持专属角色", readConverterExp = "0=否,1=是")
    private String exclusiveRoleEnabled;

    /** 是否支持聊天导出（0否 1是） */
    @Excel(name = "是否支持聊天导出", readConverterExp = "0=否,1=是")
    private String chatExportEnabled;

    /** 是否享受优先客服（0否 1是） */
    @Excel(name = "是否享受优先客服", readConverterExp = "0=否,1=是")
    private String prioritySupportEnabled;

    /** 月费价格（分） */
    @Excel(name = "月费价格")
    private Long monthlyPrice;

    /** 季费价格（分） */
    @Excel(name = "季费价格")
    private Long quarterlyPrice;

    /** 年费价格（分） */
    @Excel(name = "年费价格")
    private Long yearlyPrice;

    /** 等级描述 */
    @Excel(name = "等级描述")
    private String levelDescription;

    /** 状态（0停用 1正常） */
    @Excel(name = "状态", readConverterExp = "0=停用,1=正常")
    private String status;

    public void setLevelId(Long levelId)
    {
        this.levelId = levelId;
    }

    public Long getLevelId()
    {
        return levelId;
    }

    public void setLevelCode(Integer levelCode)
    {
        this.levelCode = levelCode;
    }

    public Integer getLevelCode()
    {
        return levelCode;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelDisplayName(String levelDisplayName)
    {
        this.levelDisplayName = levelDisplayName;
    }

    public String getLevelDisplayName()
    {
        return levelDisplayName;
    }

    public void setDailyChatLimit(Integer dailyChatLimit)
    {
        this.dailyChatLimit = dailyChatLimit;
    }

    public Integer getDailyChatLimit()
    {
        return dailyChatLimit;
    }

    public void setDailyImageLimit(Integer dailyImageLimit)
    {
        this.dailyImageLimit = dailyImageLimit;
    }

    public Integer getDailyImageLimit()
    {
        return dailyImageLimit;
    }

    public void setCustomRoleQuota(Integer customRoleQuota)
    {
        this.customRoleQuota = customRoleQuota;
    }

    public Integer getCustomRoleQuota()
    {
        return customRoleQuota;
    }

    public void setVoiceEnabled(String voiceEnabled)
    {
        this.voiceEnabled = voiceEnabled;
    }

    public String getVoiceEnabled()
    {
        return voiceEnabled;
    }

    public void setImageEnabled(String imageEnabled)
    {
        this.imageEnabled = imageEnabled;
    }

    public String getImageEnabled()
    {
        return imageEnabled;
    }

    public void setExclusiveRoleEnabled(String exclusiveRoleEnabled)
    {
        this.exclusiveRoleEnabled = exclusiveRoleEnabled;
    }

    public String getExclusiveRoleEnabled()
    {
        return exclusiveRoleEnabled;
    }

    public void setChatExportEnabled(String chatExportEnabled)
    {
        this.chatExportEnabled = chatExportEnabled;
    }

    public String getChatExportEnabled()
    {
        return chatExportEnabled;
    }

    public void setPrioritySupportEnabled(String prioritySupportEnabled)
    {
        this.prioritySupportEnabled = prioritySupportEnabled;
    }

    public String getPrioritySupportEnabled()
    {
        return prioritySupportEnabled;
    }

    public void setMonthlyPrice(Long monthlyPrice)
    {
        this.monthlyPrice = monthlyPrice;
    }

    public Long getMonthlyPrice()
    {
        return monthlyPrice;
    }

    public void setQuarterlyPrice(Long quarterlyPrice)
    {
        this.quarterlyPrice = quarterlyPrice;
    }

    public Long getQuarterlyPrice()
    {
        return quarterlyPrice;
    }

    public void setYearlyPrice(Long yearlyPrice)
    {
        this.yearlyPrice = yearlyPrice;
    }

    public Long getYearlyPrice()
    {
        return yearlyPrice;
    }

    public void setLevelDescription(String levelDescription)
    {
        this.levelDescription = levelDescription;
    }

    public String getLevelDescription()
    {
        return levelDescription;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("levelId", getLevelId())
            .append("levelCode", getLevelCode())
            .append("levelName", getLevelName())
            .append("levelDisplayName", getLevelDisplayName())
            .append("dailyChatLimit", getDailyChatLimit())
            .append("dailyImageLimit", getDailyImageLimit())
            .append("customRoleQuota", getCustomRoleQuota())
            .append("voiceEnabled", getVoiceEnabled())
            .append("imageEnabled", getImageEnabled())
            .append("exclusiveRoleEnabled", getExclusiveRoleEnabled())
            .append("chatExportEnabled", getChatExportEnabled())
            .append("prioritySupportEnabled", getPrioritySupportEnabled())
            .append("monthlyPrice", getMonthlyPrice())
            .append("quarterlyPrice", getQuarterlyPrice())
            .append("yearlyPrice", getYearlyPrice())
            .append("levelDescription", getLevelDescription())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}