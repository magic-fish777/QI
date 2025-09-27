package com.qiqiplay.ai.service.impl;

import com.qiqiplay.ai.service.EmailService;
import com.qiqiplay.ai.util.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 邮箱服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private MailHelper mailHelper;

    @Override
    public boolean sendVerificationCode(String email, String code) {
        String title = "QiQiPlay - 邮箱验证码";
        String html = String.format(
            "<html><body>" +
            "<h2>QiQiPlay 邮箱验证</h2>" +
            "<p>您的验证码是：<strong style='font-size: 18px; color: #007bff;'>%s</strong></p>" +
            "<p style='color: #666;'>验证码5分钟内有效，请勿泄露给他人。</p>" +
            "<p style='color: #999; font-size: 12px;'>如果您没有请求此验证码，请忽略此邮件。</p>" +
            "</body></html>",
            code
        );

        return mailHelper.sendHtml(email, title, html);
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}