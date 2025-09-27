package com.qiqiplay.ai.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {

    /**
     * 存储验证码
     * @param email 邮箱
     * @param code 验证码
     */
    void storeCode(String email, String code);

    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code);

    /**
     * 删除验证码
     * @param email 邮箱
     */
    void removeCode(String email);
}