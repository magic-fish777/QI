package com.qiqiplay.web.controller.chat.service;

import com.qiqiplay.web.controller.chat.domain.TextChatRequest;
import com.qiqiplay.web.controller.chat.domain.AudioChatRequest;
import com.qiqiplay.web.controller.chat.domain.ChatResponse;

/**
 * 聊天服务接口
 *
 * @author qiqiplay
 */
public interface IChatService
{
    /**
     * 处理文本聊天
     *
     * @param request 文本聊天请求
     * @return 聊天响应
     */
    ChatResponse processTextChat(TextChatRequest request);

    /**
     * 处理语音聊天
     *
     * @param request 语音聊天请求
     * @return 聊天响应
     */
    ChatResponse processAudioChat(AudioChatRequest request);
}