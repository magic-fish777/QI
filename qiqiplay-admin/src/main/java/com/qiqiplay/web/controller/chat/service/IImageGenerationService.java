package com.qiqiplay.web.controller.chat.service;

import java.util.Map;

/**
 * 图片生成服务接口
 *
 * @author qiqiplay
 */
public interface IImageGenerationService
{
    /**
     * 生成图片
     *
     * @param userId 用户ID
     * @param prompt 图片描述
     * @param style 图片风格
     * @param size 图片尺寸
     * @return 生成结果
     */
    Map<String, Object> generateImage(Long userId, String prompt, String style, String size);

    /**
     * 获取用户图片生成历史
     *
     * @param userId 用户ID
     * @return 历史记录
     */
    Map<String, Object> getUserImageHistory(Long userId);

    /**
     * 检查用户图片生成权限
     *
     * @param userId 用户ID
     * @return 权限检查结果
     */
    Map<String, Object> checkImageGenerationPermission(Long userId);
}