package com.qiqiplay.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 语音缓存对象 sys_voice_cache
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysVoiceCache extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 语音ID */
    private Long voiceId;

    /** 用户ID（sys_user.user_id） */
    @Excel(name = "用户ID", readConverterExp = "s=ys_user.user_id")
    private Long userId;

    /** 语音类型（1用户 2AI） */
    @Excel(name = "语音类型", readConverterExp = "1=用户,2=AI")
    private String voiceType;

    /** 关联聊天记录ID */
    @Excel(name = "关联聊天记录ID")
    private Long relatedRecordId;

    /** 语音文件URL */
    @Excel(name = "语音文件URL")
    private String voiceUrl;

    /** 时长（秒） */
    @Excel(name = "时长", readConverterExp = "秒=")
    private Long voiceDuration;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setVoiceId(Long voiceId) 
    {
        this.voiceId = voiceId;
    }

    public Long getVoiceId() 
    {
        return voiceId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setVoiceType(String voiceType) 
    {
        this.voiceType = voiceType;
    }

    public String getVoiceType() 
    {
        return voiceType;
    }

    public void setRelatedRecordId(Long relatedRecordId) 
    {
        this.relatedRecordId = relatedRecordId;
    }

    public Long getRelatedRecordId() 
    {
        return relatedRecordId;
    }

    public void setVoiceUrl(String voiceUrl) 
    {
        this.voiceUrl = voiceUrl;
    }

    public String getVoiceUrl() 
    {
        return voiceUrl;
    }

    public void setVoiceDuration(Long voiceDuration) 
    {
        this.voiceDuration = voiceDuration;
    }

    public Long getVoiceDuration() 
    {
        return voiceDuration;
    }

    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }

    public void setExpireTime(Date expireTime) 
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime() 
    {
        return expireTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("voiceId", getVoiceId())
            .append("userId", getUserId())
            .append("voiceType", getVoiceType())
            .append("relatedRecordId", getRelatedRecordId())
            .append("voiceUrl", getVoiceUrl())
            .append("voiceDuration", getVoiceDuration())
            .append("uploadTime", getUploadTime())
            .append("expireTime", getExpireTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
