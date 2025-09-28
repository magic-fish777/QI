package com.qiqiplay.web.controller.chat;

import com.qiqiplay.common.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.common.core.domain.model.LoginUser;
import java.util.Base64;
import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.web.controller.chat.domain.TextChatRequest;
import com.qiqiplay.web.controller.chat.domain.AudioChatRequest;
import com.qiqiplay.web.controller.chat.domain.ChatResponse;
import com.qiqiplay.web.controller.chat.service.IChatService;
import com.qiqiplay.common.utils.OssUtils;

/**
 * 聊天接口控制器
 *
 * @author qiqiplay
 */
@Anonymous
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController
{
    @Autowired
    private IChatService chatService;

    @Autowired
    private OssUtils ossUtils;

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
     * @param audioFile 音频文件
     * @param role 角色名称
     * @return 聊天响应结果
     */
    @PostMapping("/audio")
    public AjaxResult audioChat(@RequestParam("audio") MultipartFile audioFile,
                               @RequestParam("role") String role)
    {
        try
        {
            // 将音频文件转换为Base64编码
            byte[] audioBytes = audioFile.getBytes();
            String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

            // 创建AudioChatRequest对象
            AudioChatRequest request = new AudioChatRequest(audioBase64, role);
            request.setOriginalAudioData(audioBytes);
            request.setOriginalFileName(audioFile.getOriginalFilename());

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
