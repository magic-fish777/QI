package com.qiqiplay.ai.controller;

import java.util.Set;

import com.qiqiplay.ai.service.ISysFrontUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qiqiplay.common.core.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqiplay.common.constant.Constants;
import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.common.utils.StringUtils;
import com.qiqiplay.ai.domain.model.AiFrontLoginUser;
import com.qiqiplay.ai.domain.model.AiFrontLoginBody;
import com.qiqiplay.ai.domain.model.AiFrontRegisterBody;
import com.qiqiplay.ai.domain.model.EmailCodeRequest;

/**
 * AI前台认证控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/ai/auth")
public class AiFrontAuthController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiFrontAuthController.class);

    @Autowired
    private ISysFrontUserService aiFrontAuthService;

    /**
     * AI前台用户登录
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody AiFrontLoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();

        log.info("==> 收到登录请求, 用户名: [{}], 登录方式: [{}]", loginBody.getUsername(), loginBody.getLoginType());

        // 基础参数验证
        if (StringUtils.isEmpty(loginBody.getUsername()))
        {
            log.warn("==> 用户名为空");
            return error("用户名不能为空");
        }

        // 根据登录方式进行验证
        String token;
        try {
            if ("email".equals(loginBody.getLoginType()))
            {
                // 邮箱验证码登录
                if (StringUtils.isEmpty(loginBody.getCode()))
                {
                    log.warn("==> 邮箱登录验证码为空");
                    return error("验证码不能为空");
                }
                log.info("==> 进行邮箱验证码登录");
                token = aiFrontAuthService.loginByEmail(loginBody.getUsername(), loginBody.getCode());
            }
            else
            {
                // 账号密码登录 (默认方式)
                if (StringUtils.isEmpty(loginBody.getPassword()))
                {
                    log.warn("==> 账号密码登录密码为空");
                    return error("密码不能为空");
                }
                log.info("==> 进行账号密码登录");
                token = aiFrontAuthService.login(loginBody.getUsername(), loginBody.getPassword());
            }
        } catch (Exception e) {
            return error(e.getMessage());
        }

        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * AI前台用户注册
     *
     * @param registerBody 注册信息
     * @return 结果
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody AiFrontRegisterBody registerBody)
    {
        return aiFrontAuthService.register(registerBody);
    }

    /**
     * 获取AI前台用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        log.info("==> 获取AI前台用户信息请求");
        try {
            AjaxResult result = aiFrontAuthService.getUserInfo();
            log.info("==> 获取用户信息结果: {}", result.get("code"));
            return result;
        } catch (Exception e) {
            log.error("==> 获取用户信息失败:", e);
            return error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮箱验证码
     *
     * @param request 包含邮箱地址的请求体
     * @return 结果
     */
    @PostMapping("/sendCode")
    public AjaxResult sendEmailCode(@RequestBody EmailCodeRequest request)
    {
        log.info("==> 收到发送邮箱验证码请求");
        log.info("==> 请求对象: {}", request);

        if (request == null) {
            log.warn("==> 请求对象为空");
            return error("请求参数不能为空");
        }

        log.info("==> 请求中的邮箱: [{}]", request.getEmail());

        if (StringUtils.isEmpty(request.getEmail())) {
            log.warn("==> 邮箱为空");
            return error("邮箱不能为空");
        }

        String email = request.getEmail().trim();
        log.info("==> 处理后的邮箱: [{}]", email);

        if (StringUtils.isEmpty(email)) {
            log.warn("==> 邮箱去空格后为空");
            return error("邮箱不能为空");
        }

        String msg = aiFrontAuthService.sendEmailCode(email);
        boolean success = StringUtils.isEmpty(msg);
        log.info("==> 邮箱验证码发送结果: {}, 消息: {}", success ? "成功" : "失败", msg);

        return success ? success() : error(msg);
    }

    /**
     * 退出登录
     *
     * @return 结果
     */
    @PostMapping("/logout")
    public AjaxResult logout()
    {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                aiFrontAuthService.logout(loginUser.getToken());
            }
            return success();
        } catch (Exception e) {
            return success(); // 即使退出失败也返回成功，因为前端会清除token
        }
    }
}
