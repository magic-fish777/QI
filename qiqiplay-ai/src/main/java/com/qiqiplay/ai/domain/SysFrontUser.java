package com.qiqiplay.ai.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 前台用户信息（精简版）对象 sys_front_user
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysFrontUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户账号（登录用） */
    @Excel(name = "用户账号", readConverterExp = "登=录用")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 用户邮箱（用于登录/找回密码） */
    @Excel(name = "用户邮箱", readConverterExp = "用=于登录/找回密码")
    private String email;

    /** 手机号码（用于登录/验证） */
    @Excel(name = "手机号码", readConverterExp = "用=于登录/验证")
    private String phonenumber;

    /** 用户性别（0男 1女 2未知） */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /** 头像地址 */
    @Excel(name = "头像地址")
    private String avatar;

    /** 密码（加密存储） */
    @Excel(name = "密码", readConverterExp = "加=密存储")
    private String password;

    /** 密码盐值 */
    @Excel(name = "密码盐值")
    private String passwordSalt;

    /** 账号状态（0正常 1停用） */
    @Excel(name = "账号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 最后登录IP */
    @Excel(name = "最后登录IP")
    private String lastLoginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastLoginTime;

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setPhonenumber(String phonenumber) 
    {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() 
    {
        return phonenumber;
    }

    public void setSex(String sex) 
    {
        this.sex = sex;
    }

    public String getSex() 
    {
        return sex;
    }

    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }

    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    public void setPasswordSalt(String passwordSalt) 
    {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordSalt() 
    {
        return passwordSalt;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setLastLoginIp(String lastLoginIp) 
    {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastLoginIp() 
    {
        return lastLoginIp;
    }

    public void setLastLoginTime(Date lastLoginTime) 
    {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastLoginTime() 
    {
        return lastLoginTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("email", getEmail())
            .append("phonenumber", getPhonenumber())
            .append("sex", getSex())
            .append("avatar", getAvatar())
            .append("password", getPassword())
            .append("passwordSalt", getPasswordSalt())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("lastLoginIp", getLastLoginIp())
            .append("lastLoginTime", getLastLoginTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
