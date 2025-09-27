package com.qiqiplay.ai.domain.model;

/**
 * AI前台用户登录对象
 *
 * @author qiqiplay
 */
public class AiFrontLoginBody
{
    /**
     * 用户名/邮箱
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 登录方式 (account: 账号密码登录, email: 邮箱验证码登录)
     */
    private String loginType;

    /**
     * 验证码 (邮箱登录时使用)
     */
    private String code;

    /**
     * 记住我
     */
    private Boolean rememberMe;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getLoginType()
    {
        return loginType;
    }

    public void setLoginType(String loginType)
    {
        this.loginType = loginType;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Boolean getRememberMe()
    {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe)
    {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "AiFrontLoginBody{" +
                "username='" + username + '\'' +
                ", loginType='" + loginType + '\'' +
                ", code='" + code + '\'' +
                ", rememberMe=" + rememberMe +
                ", password='[PROTECTED]'" +
                '}';
    }
}