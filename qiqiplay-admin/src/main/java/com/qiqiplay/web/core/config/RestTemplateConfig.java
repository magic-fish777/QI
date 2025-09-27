package com.qiqiplay.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 *
 * @author qiqiplay
 */
@Configuration
public class RestTemplateConfig
{
    /**
     * 创建RestTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}