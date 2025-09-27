package com.qiqiplay.web.controller.chat.domain;

/**
 * 聊天响应实体
 *
 * @author qiqiplay
 */
public class ChatResponse
{
    private String role;
    private String user;
    private String reply;
    private String audioFile;

    public ChatResponse()
    {
    }

    public ChatResponse(String role, String user, String reply, String audioFile)
    {
        this.role = role;
        this.user = user;
        this.reply = reply;
        this.audioFile = audioFile;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getReply()
    {
        return reply;
    }

    public void setReply(String reply)
    {
        this.reply = reply;
    }

    public String getAudioFile()
    {
        return audioFile;
    }

    public void setAudioFile(String audioFile)
    {
        this.audioFile = audioFile;
    }

    @Override
    public String toString()
    {
        return "ChatResponse{" +
                "role='" + role + '\'' +
                ", user='" + user + '\'' +
                ", reply='" + reply + '\'' +
                ", audioFile='" + audioFile + '\'' +
                '}';
    }
}