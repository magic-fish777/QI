package com.qiqiplay.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.qiqiplay.common.config.OssConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 *
 * @author qiqiplay
 */
@Component
public class OssUtils {

    private static final Logger logger = LoggerFactory.getLogger(OssUtils.class);

    @Autowired
    private OssConfig ossConfig;

    /**
     * 上传音频文件到OSS
     *
     * @param audioFile 音频文件
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadAudioFile(MultipartFile audioFile) throws IOException {
        return uploadAudioFile(audioFile.getBytes(), getFileExtension(audioFile.getOriginalFilename()));
    }

    /**
     * 上传音频文件字节数组到OSS
     *
     * @param audioBytes 音频文件字节数组
     * @param fileExtension 文件扩展名（如：wav, mp3, m4a）
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadAudioFile(byte[] audioBytes, String fileExtension) throws IOException {
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(),
                    ossConfig.getAccessKey(), ossConfig.getSecretKey());

            // 生成文件名
            String fileName = generateAudioFileName(fileExtension);

            // 构造完整的对象名称（包含路径）
            String objectName = ossConfig.getAudioPath() + fileName;

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectName,
                    new ByteArrayInputStream(audioBytes)
            );

            // 上传文件
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            if (result != null) {
                // 构造文件访问URL
                String fileUrl = buildFileUrl(objectName);
                logger.info("音频文件上传成功: fileName={}, url={}", fileName, fileUrl);
                return fileUrl;
            } else {
                throw new IOException("文件上传失败");
            }

        } catch (Exception e) {
            logger.error("音频文件上传到OSS失败: {}", e.getMessage(), e);
            throw new IOException("文件上传失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成音频文件名
     *
     * @param fileExtension 文件扩展名
     * @return 生成的文件名
     */
    private String generateAudioFileName(String fileExtension) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 确保扩展名格式正确
        if (fileExtension != null && !fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension;
        }
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            fileExtension = ".wav"; // 默认wav格式
        }

        return dateStr + "/" + uuid + fileExtension;
    }

    /**
     * 构造文件访问URL
     *
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    private String buildFileUrl(String objectName) {
        if (ossConfig.getDomain() != null && !ossConfig.getDomain().trim().isEmpty()) {
            // 使用自定义域名
            String domain = ossConfig.getDomain();
            if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                domain = "https://" + domain;
            }
            if (domain.endsWith("/")) {
                domain = domain.substring(0, domain.length() - 1);
            }
            return domain + "/" + objectName;
        } else {
            // 使用默认域名
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不包含点）
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "wav"; // 默认wav格式
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return "wav"; // 默认wav格式
    }

    /**
     * 删除OSS上的文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }

        OSS ossClient = null;
        try {
            // 从URL中提取对象名称
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                logger.warn("无法从URL中提取对象名称: {}", fileUrl);
                return false;
            }

            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(),
                    ossConfig.getAccessKey(), ossConfig.getSecretKey());

            // 删除文件
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            logger.info("OSS文件删除成功: objectName={}", objectName);
            return true;

        } catch (Exception e) {
            logger.error("OSS文件删除失败: url={}, error={}", fileUrl, e.getMessage(), e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从文件URL中提取对象名称
     *
     * @param fileUrl 文件URL
     * @return 对象名称
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        try {
            if (fileUrl.contains("/" + ossConfig.getAudioPath())) {
                int index = fileUrl.indexOf("/" + ossConfig.getAudioPath());
                return fileUrl.substring(index + 1); // 去掉开头的"/"
            }
            return null;
        } catch (Exception e) {
            logger.error("提取对象名称失败: {}", e.getMessage());
            return null;
        }
    }
}