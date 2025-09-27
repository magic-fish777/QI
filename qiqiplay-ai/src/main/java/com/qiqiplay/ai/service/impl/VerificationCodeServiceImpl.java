package com.qiqiplay.ai.service.impl;

import com.qiqiplay.ai.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "verification_code:";
    private static final int EXPIRATION_TIME = 5; // 5分钟过期

    @Override
    public void storeCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        return storedCode != null && storedCode.equals(code);
    }

    @Override
    public void removeCode(String email) {
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }
}