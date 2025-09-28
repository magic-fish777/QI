package com.qiqiplay.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 用户每日使用统计对象 sys_user_daily_usage
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
public class SysUserDailyUsage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 使用记录ID */
    private Long usageId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 使用日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "使用日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date usageDate;

    /** 聊天次数 */
    @Excel(name = "聊天次数")
    private Integer chatCount;

    /** 图片生成次数 */
    @Excel(name = "图片生成次数")
    private Integer imageCount;

    /** 语音使用次数 */
    @Excel(name = "语音使用次数")
    private Integer voiceCount;

    public void setUsageId(Long usageId)
    {
        this.usageId = usageId;
    }

    public Long getUsageId()
    {
        return usageId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUsageDate(Date usageDate)
    {
        this.usageDate = usageDate;
    }

    public Date getUsageDate()
    {
        return usageDate;
    }
    public void setChatCount(Integer chatCount)
    {
        this.chatCount = chatCount;
    }

    public Integer getChatCount()
    {
        return chatCount;
    }
    public void setImageCount(Integer imageCount)
    {
        this.imageCount = imageCount;
    }

    public Integer getImageCount()
    {
        return imageCount;
    }
    public void setVoiceCount(Integer voiceCount)
    {
        this.voiceCount = voiceCount;
    }

    public Integer getVoiceCount()
    {
        return voiceCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("usageId", getUsageId())
            .append("userId", getUserId())
            .append("usageDate", getUsageDate())
            .append("chatCount", getChatCount())
            .append("imageCount", getImageCount())
            .append("voiceCount", getVoiceCount())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}