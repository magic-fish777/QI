package com.qiqiplay.web.controller.chat.domain;

/**
 * 语音聊天请求实体
 *
 * @author qiqiplay
 */
public class AudioChatRequest
{
    private String audio;
    private String role;

    public AudioChatRequest()
    {
    }

    public AudioChatRequest(String audio, String role)
    {
        this.audio = audio;
        this.role = role;
    }

    public String getAudio()
    {
        return audio;
    }

    public void setAudio(String audio)
    {
        this.audio = audio;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    @Override
    public String toString()
    {
        return "AudioChatRequest{" +
                "audio='" + audio + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}