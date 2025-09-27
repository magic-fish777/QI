package com.qiqiplay.web.controller.chat.domain;

/**
 * 文本聊天请求实体
 *
 * @author qiqiplay
 */
public class TextChatRequest
{
    private String text;
    private String role;

    public TextChatRequest()
    {
    }

    public TextChatRequest(String text, String role)
    {
        this.text = text;
        this.role = role;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
        return "TextChatRequest{" +
                "text='" + text + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}