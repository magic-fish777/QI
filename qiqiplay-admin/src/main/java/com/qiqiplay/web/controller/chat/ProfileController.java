package com.qiqiplay.web.controller.chat;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.utils.OssUtils;
import com.qiqiplay.web.controller.chat.utils.ChatSecurityUtils;
import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.service.ISysFrontUserService;

/**
 * 前台用户个人资料控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/chat/profile")
public class ProfileController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ISysFrontUserService sysFrontUserService;

    @Autowired
    private OssUtils ossUtils;

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public AjaxResult getUserInfo()
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 查询用户信息
            SysFrontUser user = sysFrontUserService.selectSysFrontUserByUserId(currentUserId);
            if (user == null)
            {
                return error("用户不存在");
            }

            // 移除敏感信息
            user.setPassword(null);
            user.setPasswordSalt(null);

            return success(user);
        }
        catch (Exception e)
        {
            log.error("获取用户信息失败", e);
            return error("获取用户信息失败");
        }
    }

    /**
     * 上传用户头像
     *
     * @param avatarFile 头像文件
     * @return 上传结果
     */
    @PostMapping("/avatar/upload")
    public AjaxResult uploadAvatar(@RequestParam("avatar") MultipartFile avatarFile)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 验证文件
            if (avatarFile == null || avatarFile.isEmpty())
            {
                return error("请选择头像文件");
            }

            // 验证文件类型
            String originalFilename = avatarFile.getOriginalFilename();
            if (originalFilename == null || !isValidImageFile(originalFilename))
            {
                return error("只支持jpg、jpeg、png、gif格式的图片");
            }

            // 验证文件大小（限制5MB）
            if (avatarFile.getSize() > 5 * 1024 * 1024)
            {
                return error("头像文件不能超过5MB");
            }

            log.info("开始上传头像: userId={}, fileName={}, size={}",
                    currentUserId, originalFilename, avatarFile.getSize());

            // 上传到OSS
            String avatarUrl = ossUtils.uploadAvatarImage(avatarFile);

            log.info("头像上传成功: userId={}, avatarUrl={}", currentUserId, avatarUrl);

            // 更新用户头像
            SysFrontUser user = new SysFrontUser();
            user.setUserId(currentUserId);
            user.setAvatar(avatarUrl);

            int result = sysFrontUserService.updateSysFrontUser(user);
            if (result <= 0)
            {
                // 如果数据库更新失败，尝试删除已上传的文件
                try {
                    ossUtils.deleteFile(avatarUrl);
                } catch (Exception deleteEx) {
                    log.warn("删除OSS文件失败: {}", deleteEx.getMessage());
                }
                return error("更新头像失败");
            }

            log.info("用户头像更新成功: userId={}, avatarUrl={}", currentUserId, avatarUrl);

            return success("头像上传成功", avatarUrl);
        }
        catch (IOException e)
        {
            log.error("头像上传失败: userId={}, error={}",
                    ChatSecurityUtils.getCurrentUserId(), e.getMessage(), e);
            return error("头像上传失败：" + e.getMessage());
        }
        catch (Exception e)
        {
            log.error("头像上传失败", e);
            return error("头像上传失败，请稍后重试");
        }
    }

    /**
     * 验证是否为有效的图片文件
     *
     * @param filename 文件名
     * @return 是否为有效图片
     */
    private boolean isValidImageFile(String filename)
    {
        if (filename == null || filename.trim().isEmpty())
        {
            return false;
        }

        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".jpg") ||
               lowerName.endsWith(".jpeg") ||
               lowerName.endsWith(".png") ||
               lowerName.endsWith(".gif");
    }
}