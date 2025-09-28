package com.qiqiplay.web.controller.chat;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.web.controller.chat.service.IImageGenerationService;
import com.qiqiplay.web.controller.chat.utils.ChatSecurityUtils;

/**
 * AI图片生成控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/chat/image")
public class ImageController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private IImageGenerationService imageGenerationService;

    /**
     * 生成图片
     *
     * @param requestData 生成请求数据
     * @return 生成结果
     */
    @PostMapping("/generate")
    public AjaxResult generateImage(@RequestBody Map<String, Object> requestData)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            String prompt = (String) requestData.get("prompt");
            String style = (String) requestData.get("style"); // 图片风格（可选）
            String size = (String) requestData.get("size");   // 图片尺寸（可选）

            if (prompt == null || prompt.trim().isEmpty())
            {
                return error("请输入图片描述");
            }

            // 调用图片生成服务
            Map<String, Object> result = imageGenerationService.generateImage(currentUserId, prompt, style, size);

            return success("图片生成成功", result);
        }
        catch (RuntimeException e)
        {
            log.error("图片生成失败: {}", e.getMessage(), e);
            return error(e.getMessage());
        }
        catch (Exception e)
        {
            log.error("图片生成异常", e);
            return error("图片生成失败，请稍后重试");
        }
    }

    /**
     * 获取图片生成历史
     *
     * @return 历史记录
     */
    @PostMapping("/history")
    public AjaxResult getImageHistory()
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 获取用户图片生成历史
            Map<String, Object> result = imageGenerationService.getUserImageHistory(currentUserId);

            return success(result);
        }
        catch (Exception e)
        {
            log.error("获取图片历史失败", e);
            return error("获取历史记录失败");
        }
    }
}