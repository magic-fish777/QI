package com.qiqiplay.web.controller.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.web.controller.chat.domain.TextChatRequest;
import com.qiqiplay.web.controller.chat.domain.AudioChatRequest;
import com.qiqiplay.web.controller.chat.domain.ChatResponse;
import com.qiqiplay.web.controller.chat.service.IChatService;

/**
 * 聊天接口控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController
{
    @Autowired
    private IChatService chatService;

    /**
     * 文本聊天接口
     *
     * @param request 文本聊天请求
     * @return 聊天响应结果
     */
    @PostMapping("/text")
    public AjaxResult textChat(@RequestBody TextChatRequest request)
    {
        try
        {
            ChatResponse response = chatService.processTextChat(request);
            return success(response);
        }
        catch (Exception e)
        {
            logger.error("文本聊天处理失败", e);
            return error("聊天处理失败：" + e.getMessage());
        }
    }

    /**
     * 语音聊天接口
     *
     * @param request 语音聊天请求
     * @return 聊天响应结果
     */
    @PostMapping("/audio")
    public AjaxResult audioChat(@RequestBody AudioChatRequest request)
    {
        try
        {
            ChatResponse response = chatService.processAudioChat(request);
            return success(response);
        }
        catch (Exception e)
        {
            logger.error("语音聊天处理失败", e);
            return error("聊天处理失败：" + e.getMessage());
        }
    }
}