package com.qiqiplay.web.controller.chat.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.qiqiplay.common.core.domain.model.LoginUser;
import com.qiqiplay.common.utils.StringUtils;

/**
 * 前台聊天安全工具类
 * 兼容前台和后台用户
 *
 * @author qiqiplay
 */
public class ChatSecurityUtils
{
    /**
     * 获取当前用户ID（兼容前台和后台用户）
     */
    public static Long getCurrentUserId()
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null)
            {
                return null;
            }

            Object principal = authentication.getPrincipal();

            // 如果是LoginUser类型（包括转换后的前台用户）
            if (principal instanceof LoginUser)
            {
                LoginUser loginUser = (LoginUser) principal;
                return loginUser.getUserId();
            }

            // 其他类型的Principal，暂不支持
            return null;
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: 获取用户ID失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户名（兼容前台和后台用户）
     */
    public static String getCurrentUsername()
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null)
            {
                return null;
            }

            Object principal = authentication.getPrincipal();

            // 如果是LoginUser类型
            if (principal instanceof LoginUser)
            {
                LoginUser loginUser = (LoginUser) principal;
                // 安全获取用户名，避免NPE
                if (loginUser.getUser() != null)
                {
                    return loginUser.getUser().getUserName();
                }
                return "user_" + loginUser.getUserId();
            }

            return authentication.getName();
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: 获取用户名失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 检查用户是否已登录
     */
    public static boolean isAuthenticated()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal());
    }
}