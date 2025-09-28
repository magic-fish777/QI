package com.qiqiplay.web.controller.chat.event;

import org.springframework.context.ApplicationEvent;

/**
 * 聊天记录事件
 *
 * @author qiqiplay
 */
public class ChatRecordEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 用户输入类型（1-文本，2-语音）
     */
    private String userInputType;

    /**
     * 用户输入内容
     */
    private String userInputContent;

    /**
     * AI回复内容
     */
    private String aiReplyContent;

    /**
     * 聊天语言
     */
    private String chatLanguage;

    /**
     * 用户输入音频文件URL（如果是语音输入的话）
     */
    private String userAudioFileUrl;

    /**
     * AI回复音频文件URL（如果有的话）
     */
    private String aiAudioFileUrl;

    public ChatRecordEvent(Object source, Long userId, Long roleId, String roleName,
                          String userInputType, String userInputContent, String aiReplyContent,
                          String chatLanguage, String userAudioFileUrl, String aiAudioFileUrl) {
        super(source);
        this.userId = userId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.userInputType = userInputType;
        this.userInputContent = userInputContent;
        this.aiReplyContent = aiReplyContent;
        this.chatLanguage = chatLanguage;
        this.userAudioFileUrl = userAudioFileUrl;
        this.aiAudioFileUrl = aiAudioFileUrl;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserInputType() {
        return userInputType;
    }

    public void setUserInputType(String userInputType) {
        this.userInputType = userInputType;
    }

    public String getUserInputContent() {
        return userInputContent;
    }

    public void setUserInputContent(String userInputContent) {
        this.userInputContent = userInputContent;
    }

    public String getAiReplyContent() {
        return aiReplyContent;
    }

    public void setAiReplyContent(String aiReplyContent) {
        this.aiReplyContent = aiReplyContent;
    }

    public String getChatLanguage() {
        return chatLanguage;
    }

    public void setChatLanguage(String chatLanguage) {
        this.chatLanguage = chatLanguage;
    }

    public String getUserAudioFileUrl() {
        return userAudioFileUrl;
    }

    public void setUserAudioFileUrl(String userAudioFileUrl) {
        this.userAudioFileUrl = userAudioFileUrl;
    }

    public String getAiAudioFileUrl() {
        return aiAudioFileUrl;
    }

    public void setAiAudioFileUrl(String aiAudioFileUrl) {
        this.aiAudioFileUrl = aiAudioFileUrl;
    }

    @Override
    public String toString() {
        return "ChatRecordEvent{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", userInputType='" + userInputType + '\'' +
                ", userInputContent='" + userInputContent + '\'' +
                ", aiReplyContent='" + aiReplyContent + '\'' +
                ", chatLanguage='" + chatLanguage + '\'' +
                ", userAudioFileUrl='" + userAudioFileUrl + '\'' +
                ", aiAudioFileUrl='" + aiAudioFileUrl + '\'' +
                '}';
    }
}