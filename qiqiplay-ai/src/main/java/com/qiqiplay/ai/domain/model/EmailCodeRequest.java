package com.qiqiplay.ai.domain.model;

/**
 * 发送邮箱验证码请求体
 */
public class EmailCodeRequest {

    /**
     * 邮箱地址
     */
    private String email;

    public EmailCodeRequest() {}

    public EmailCodeRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailCodeRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}