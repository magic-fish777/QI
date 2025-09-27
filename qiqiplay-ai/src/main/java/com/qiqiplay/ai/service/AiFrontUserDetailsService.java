package com.qiqiplay.ai.service;

import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.domain.model.AiFrontLoginUser;
import com.qiqiplay.ai.mapper.SysFrontUserMapper;
import com.qiqiplay.common.core.domain.entity.SysUser;
import com.qiqiplay.common.core.domain.model.LoginUser;
import com.qiqiplay.common.exception.ServiceException;
import com.qiqiplay.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * AI前端用户验证处理
 *
 * @author qiqiplay
 */
@Service("aiFrontUserDetailsService")
public class AiFrontUserDetailsService implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(AiFrontUserDetailsService.class);

    @Autowired
    private SysFrontUserMapper frontUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysFrontUser frontUser = frontUserMapper.selectSysFrontUserByUsername(username);
        if (StringUtils.isNull(frontUser))
        {
            // 如果用户名查不到，尝试用邮箱查找
            frontUser = frontUserMapper.selectSysFrontUserByEmail(username);
        }

        if (StringUtils.isNull(frontUser))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        else if ("1".equals(frontUser.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        else if ("1".equals(frontUser.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已删除");
        }

        return createAiFrontLoginUser(frontUser);
    }

    public UserDetails createAiFrontLoginUser(SysFrontUser frontUser)
    {
        // 为前台用户设置基础权限
        Set<String> permissions = new HashSet<>();
        permissions.add("ai:chat:access"); // AI聊天访问权限

        return new AiFrontLoginUser(frontUser.getUserId(), frontUser, permissions);
    }
}