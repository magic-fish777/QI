package com.qiqiplay.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 聊天记录对象 sys_chat_record
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysChatRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 用户ID（sys_user.user_id） */
    @Excel(name = "用户ID", readConverterExp = "s=ys_user.user_id")
    private Long userId;

    /** 角色ID（sys_role_ai.role_id） */
    @Excel(name = "角色ID", readConverterExp = "s=ys_role_ai.role_id")
    private Long roleId;

    /** 输入类型（1语音 2文本） */
    @Excel(name = "输入类型", readConverterExp = "1=语音,2=文本")
    private String userInputType;

    /** 用户输入内容 */
    @Excel(name = "用户输入内容")
    private String userInputContent;

    /** AI回复内容 */
    @Excel(name = "AI回复内容")
    private String aiReplyContent;

    /** 使用技能ID（sys_role_skill.skill_id） */
    @Excel(name = "使用技能ID", readConverterExp = "s=ys_role_skill.skill_id")
    private Long skillId;

    /** 聊天语言 */
    @Excel(name = "聊天语言")
    private String chatLanguage;

    /** 聊天时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "聊天时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date chatTime;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 用户昵称（关联查询） */
    private String userNickname;

    /** 用户头像（关联查询） */
    private String userAvatar;

    /** 角色名称（关联查询） */
    private String roleName;

    /** 角色头像（关联查询） */
    private String roleAvatar;

    /** 用户输入音频文件URL */
    private String userAudioFile;

    /** AI回复音频文件URL */
    private String aiAudioFile;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRoleId(Long roleId) 
    {
        this.roleId = roleId;
    }

    public Long getRoleId() 
    {
        return roleId;
    }

    public void setUserInputType(String userInputType) 
    {
        this.userInputType = userInputType;
    }

    public String getUserInputType() 
    {
        return userInputType;
    }

    public void setUserInputContent(String userInputContent) 
    {
        this.userInputContent = userInputContent;
    }

    public String getUserInputContent() 
    {
        return userInputContent;
    }

    public void setAiReplyContent(String aiReplyContent) 
    {
        this.aiReplyContent = aiReplyContent;
    }

    public String getAiReplyContent() 
    {
        return aiReplyContent;
    }

    public void setSkillId(Long skillId) 
    {
        this.skillId = skillId;
    }

    public Long getSkillId() 
    {
        return skillId;
    }

    public void setChatLanguage(String chatLanguage) 
    {
        this.chatLanguage = chatLanguage;
    }

    public String getChatLanguage() 
    {
        return chatLanguage;
    }

    public void setChatTime(Date chatTime) 
    {
        this.chatTime = chatTime;
    }

    public Date getChatTime() 
    {
        return chatTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setUserNickname(String userNickname)
    {
        this.userNickname = userNickname;
    }

    public String getUserNickname()
    {
        return userNickname;
    }

    public void setUserAvatar(String userAvatar)
    {
        this.userAvatar = userAvatar;
    }

    public String getUserAvatar()
    {
        return userAvatar;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleAvatar(String roleAvatar)
    {
        this.roleAvatar = roleAvatar;
    }

    public String getRoleAvatar()
    {
        return roleAvatar;
    }

    public void setUserAudioFile(String userAudioFile)
    {
        this.userAudioFile = userAudioFile;
    }

    public String getUserAudioFile()
    {
        return userAudioFile;
    }

    public void setAiAudioFile(String aiAudioFile)
    {
        this.aiAudioFile = aiAudioFile;
    }

    public String getAiAudioFile()
    {
        return aiAudioFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("roleId", getRoleId())
            .append("userInputType", getUserInputType())
            .append("userInputContent", getUserInputContent())
            .append("aiReplyContent", getAiReplyContent())
            .append("skillId", getSkillId())
            .append("chatLanguage", getChatLanguage())
            .append("chatTime", getChatTime())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .toString();
    }
}
