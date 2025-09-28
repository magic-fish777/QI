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
    private byte[] originalAudioData;
    private String originalFileName;

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

    public byte[] getOriginalAudioData()
    {
        return originalAudioData;
    }

    public void setOriginalAudioData(byte[] originalAudioData)
    {
        this.originalAudioData = originalAudioData;
    }

    public String getOriginalFileName()
    {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName = originalFileName;
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