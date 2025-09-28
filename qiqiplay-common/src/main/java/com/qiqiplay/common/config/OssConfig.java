package com.qiqiplay.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置类
 *
 * @author qiqiplay
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    /** OSS服务端点 */
    private String endpoint;

    /** 访问密钥ID */
    private String accessKey;

    /** 访问密钥 */
    private String secretKey;

    /** 存储桶名称 */
    private String bucketName;

    /** 文件访问域名 */
    private String domain;

    /** 音频文件存储路径前缀 */
    private String audioPath = "audio/";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}