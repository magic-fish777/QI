package com.qiqiplay.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.qiqiplay.common.core.domain.model.LoginUser;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.common.utils.StringUtils;
import com.qiqiplay.framework.web.service.TokenService;
import com.qiqiplay.ai.service.AiFrontTokenService;
import com.qiqiplay.ai.domain.model.AiFrontLoginUser;
import com.qiqiplay.common.core.domain.entity.SysUser;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;

    @Autowired(required = false)
    private AiFrontTokenService aiFrontTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        LoginUser loginUser = null;

        // 首先尝试后台管理用户token验证
        loginUser = tokenService.getLoginUser(request);

        // 如果后台用户验证失败，且有前台token服务，则尝试前台用户验证
        if (StringUtils.isNull(loginUser) && aiFrontTokenService != null)
        {
            System.out.println("DEBUG: 尝试前台用户token验证");
            AiFrontLoginUser aiFrontLoginUser = aiFrontTokenService.getLoginUser(request);
            System.out.println("DEBUG: aiFrontLoginUser = " + (aiFrontLoginUser != null ? "存在" : "null"));

            if (StringUtils.isNotNull(aiFrontLoginUser))
            {
                System.out.println("DEBUG: 前台用户ID = " + aiFrontLoginUser.getUserId());
                // 将前台用户包装成通用LoginUser格式
                loginUser = convertToLoginUser(aiFrontLoginUser);
                System.out.println("DEBUG: 转换后的loginUser = " + (loginUser != null ? "存在" : "null"));

                if (StringUtils.isNotNull(loginUser))
                {
                    System.out.println("DEBUG: 验证前台用户token");
                    aiFrontTokenService.verifyToken(aiFrontLoginUser);
                }
            }
        }

        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
        {
            // 如果是后台用户，使用TokenService验证
            if (tokenService.getLoginUser(request) != null)
            {
                tokenService.verifyToken(loginUser);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    /**
     * 将AiFrontLoginUser转换为LoginUser
     */
    private LoginUser convertToLoginUser(AiFrontLoginUser aiFrontLoginUser)
    {
        if (aiFrontLoginUser == null) return null;

        try {
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(aiFrontLoginUser.getUserId());
            loginUser.setToken(aiFrontLoginUser.getToken());
            loginUser.setLoginTime(aiFrontLoginUser.getLoginTime());
            loginUser.setExpireTime(aiFrontLoginUser.getExpireTime());

            // 创建一个简单的SysUser对象来避免NPE
            if (aiFrontLoginUser.getFrontUser() != null) {
                SysUser sysUser = new SysUser();
                sysUser.setUserId(aiFrontLoginUser.getUserId());
                sysUser.setUserName(aiFrontLoginUser.getFrontUser().getUserName());
                // 设置其他必要字段
                sysUser.setNickName(aiFrontLoginUser.getFrontUser().getNickName());
                sysUser.setEmail(aiFrontLoginUser.getFrontUser().getEmail());
                loginUser.setUser(sysUser);
            }

            // 设置权限信息（前台用户可能需要特殊处理）
            loginUser.setPermissions(aiFrontLoginUser.getPermissions());
            return loginUser;
        } catch (Exception e) {
            // 如果转换失败，返回null
            return null;
        }
    }
}
