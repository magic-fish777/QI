package com.qiqiplay.web.controller.chat.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.common.core.domain.model.LoginUser;
import com.qiqiplay.web.controller.chat.event.ChatRecordEvent;
import com.qiqiplay.ai.service.ISysRoleAiService;
import com.qiqiplay.ai.domain.SysRoleAi;
import java.util.Base64;
import java.util.List;
import com.qiqiplay.web.controller.chat.domain.TextChatRequest;
import com.qiqiplay.web.controller.chat.domain.AudioChatRequest;
import com.qiqiplay.web.controller.chat.domain.ChatResponse;
import com.qiqiplay.web.controller.chat.service.IChatService;
import com.qiqiplay.common.utils.OssUtils;
import com.qiqiplay.ai.service.ISysUserVipService;
import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.web.controller.chat.utils.ChatSecurityUtils;
import com.qiqiplay.ai.service.ISysUserDailyUsageService;
import java.util.HashMap;
import java.util.Map;

/**
 * 聊天服务实现类
 *
 * @author qiqiplay
 */
@Service
public class ChatServiceImpl implements IChatService
{
    private static final String TEXT_CHAT_URL = "http://114.132.73.165:8000/chat/text";
    private static final String AUDIO_CHAT_URL = "http://114.132.73.165:8000/chat/audio";
    private static final Logger Logger = org.slf4j.LoggerFactory.getLogger(ChatServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ISysRoleAiService sysRoleAiService;

    @Autowired
    private OssUtils ossUtils;

    @Autowired
    private ISysUserVipService sysUserVipService;

    @Autowired
    private ISysUserDailyUsageService sysUserDailyUsageService;

    @Override
    public ChatResponse processTextChat(TextChatRequest request)
    {
        try
        {
            // 检查用户文本聊天权限
            checkUserChatPermission();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                TEXT_CHAT_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            Logger.info("response: {}", response.getBody());

            ChatResponse chatResponse = parseResponse(response.getBody());

            // 记录使用次数（文本聊天）
            try {
                Long currentUserId = ChatSecurityUtils.getCurrentUserId();
                if (currentUserId != null) {
                    sysUserDailyUsageService.recordChatUsage(currentUserId, false);
                }
            } catch (Exception e) {
                Logger.warn("记录聊天使用次数失败: {}", e.getMessage());
            }

            // 异步发布聊天记录事件
            publishChatRecordEvent(request.getText(), chatResponse.getReply(),
                                  request.getRole(), "1", null, chatResponse.getAudioFile());

            return chatResponse;
        }
        catch (Exception e)
        {
            throw new RuntimeException("调用文本聊天接口失败", e);
        }
    }

    @Override
    public ChatResponse processAudioChat(AudioChatRequest request)
    {
        try
        {
            // 检查用户语音聊天权限
            checkUserVoicePermission();

            Logger.info("开始处理音频聊天请求: role={}", request.getRole());

            // 将base64编码的音频数据解码为字节数组
            byte[] audioBytes = Base64.getDecoder().decode(request.getAudio());
            Logger.info("音频数据解码完成: 字节长度={}", audioBytes.length);

            // 创建ByteArrayResource来包装音频数据
            ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
                @Override
                public String getFilename() {
                    return "audio.wav"; // 提供文件名
                }

                @Override
                public String getDescription() {
                    return "Audio file";
                }
            };

            // 构建multipart/form-data请求
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("audio", audioResource);
            parts.add("role", request.getRole());

            Logger.info("准备发送请求到AI服务: url={}, role={}", AUDIO_CHAT_URL, request.getRole());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parts, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                AUDIO_CHAT_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            Logger.info("response: {}", response.getBody());

            ChatResponse chatResponse = parseResponse(response.getBody());

            // 记录使用次数（语音聊天）
            try {
                Long currentUserId = ChatSecurityUtils.getCurrentUserId();
                if (currentUserId != null) {
                    sysUserDailyUsageService.recordChatUsage(currentUserId, true);
                }
            } catch (Exception e) {
                Logger.warn("记录语音聊天使用次数失败: {}", e.getMessage());
            }

            // 异步发布聊天记录事件（语音输入）
            // 保存用户音频到OSS用于记录
            String userAudioUrl = null;
            try {
                if (request.getOriginalAudioData() != null) {
                    String fileExtension = getFileExtension(request.getOriginalFileName());
                    userAudioUrl = ossUtils.uploadAudioFile(request.getOriginalAudioData(), fileExtension);
                    Logger.info("用户音频文件保存成功: {}", userAudioUrl);
                }
            } catch (Exception e) {
                Logger.error("保存用户音频文件失败: {}", e.getMessage(), e);
            }

            publishChatRecordEvent("[语音输入]", chatResponse.getReply(),
                                  request.getRole(), "2", userAudioUrl, chatResponse.getAudioFile());

            return chatResponse;
        }
        catch (Exception e)
        {
            Logger.error("调用语音聊天接口失败: role={}, error={}", request.getRole(), e.getMessage(), e);
            throw new RuntimeException("调用语音聊天接口失败", e);
        }
    }

    private ChatResponse parseResponse(String responseBody) throws Exception
    {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // 获取data节点
        JsonNode dataNode = jsonNode.path("data");
        if (dataNode.isMissingNode()) {
            // 如果没有data节点，尝试直接解析（兼容旧格式）
            dataNode = jsonNode;
        }

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setRole(dataNode.path("role").asText());
        chatResponse.setUser(dataNode.path("user").asText());
        chatResponse.setReply(dataNode.path("reply").asText());
        chatResponse.setAudioFile(dataNode.path("audio_file").asText());

        return chatResponse;
    }

    /**
     * 发布聊天记录事件
     *
     * @param userInputContent 用户输入内容
     * @param aiReplyContent AI回复内容
     * @param roleName 角色名称
     * @param inputType 输入类型（1-文本，2-语音）
     * @param userAudioFileUrl 用户音频文件URL
     * @param aiAudioFileUrl AI音频文件URL
     */
    private void publishChatRecordEvent(String userInputContent, String aiReplyContent,
                                       String roleName, String inputType, String userAudioFileUrl, String aiAudioFileUrl) {
        try {
            // 获取当前登录用户信息
            Long userId = null;
            String chatLanguage = "zh-CN"; // 默认中文

            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null) {
                    userId = loginUser.getUserId();
                }
            } catch (Exception e) {
                Logger.warn("获取当前用户信息失败，将使用默认值: {}", e.getMessage());
                userId = 1L; // 使用默认用户ID，或者可以从其他地方获取
            }

            // 这里暂时使用角色名称映射角色ID，实际项目中应该从数据库查询
            Long roleId = getRoleIdByName(roleName);

            // 发布聊天记录事件
            ChatRecordEvent event = new ChatRecordEvent(
                this,
                userId,
                roleId,
                roleName,
                inputType,
                userInputContent,
                aiReplyContent,
                chatLanguage,
                userAudioFileUrl,
                aiAudioFileUrl
            );

            applicationEventPublisher.publishEvent(event);
            Logger.info("聊天记录事件已发布: userId={}, roleId={}, inputType={}", userId, roleId, inputType);

        } catch (Exception e) {
            Logger.error("发布聊天记录事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据角色key获取角色ID
     *
     * @param roleKey 角色key (传参名role，但实际值为roleKey)
     * @return 角色ID
     */
    private Long getRoleIdByName(String roleKey) {
        try {
            if (roleKey == null || roleKey.trim().isEmpty()) {
                Logger.warn("角色key为空，使用默认角色ID: 1");
                return 1L;
            }

            // 构建查询条件
            SysRoleAi queryRole = new SysRoleAi();
            queryRole.setRoleKey(roleKey.trim());
            queryRole.setStatus("0"); // 只查询正常状态的角色

            // 查询角色列表
            List<SysRoleAi> roleList = sysRoleAiService.selectSysRoleAiList(queryRole);

            if (roleList != null && !roleList.isEmpty()) {
                Long roleId = roleList.get(0).getRoleId();
                Logger.info("根据角色key '{}' 找到角色ID: {}", roleKey, roleId);
                return roleId;
            } else {
                Logger.warn("未找到角色key为 '{}' 的角色信息，使用默认角色ID: 1", roleKey);
                return 1L; // 默认角色ID
            }

        } catch (Exception e) {
            Logger.error("查询角色ID时发生异常，角色key: '{}', 错误: {}", roleKey, e.getMessage(), e);
            return 1L; // 异常时返回默认角色ID
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
     * 检查用户文本聊天权限
     */
    private void checkUserChatPermission() {
        try {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                throw new RuntimeException("用户未登录，无法进行聊天");
            }

            // 获取用户VIP信息和权限
            Map<String, Object> userPrivileges = getUserPrivileges(currentUserId);

            // 检查每日聊天限制
            Integer dailyChatLimit = (Integer) userPrivileges.get("dailyChatLimit");
            if (dailyChatLimit != null && dailyChatLimit != -1) {
                // 检查用户今日的聊天次数是否达到限制
                if (sysUserDailyUsageService.hasReachedDailyChatLimit(currentUserId, dailyChatLimit)) {
                    throw new RuntimeException("您今日的聊天次数已达上限（" + dailyChatLimit + "次），请明日再试或升级会员");
                }
            }

            Logger.info("用户 {} 聊天权限检查通过，每日限制: {}", currentUserId, dailyChatLimit);

        } catch (Exception e) {
            Logger.error("检查用户聊天权限失败: {}", e.getMessage(), e);
            throw new RuntimeException("权限检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户语音聊天权限
     */
    private void checkUserVoicePermission() {
        try {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                throw new RuntimeException("用户未登录，无法进行语音聊天");
            }

            // 获取用户VIP信息和权限
            Map<String, Object> userPrivileges = getUserPrivileges(currentUserId);

            // 检查语音功能权限
            Boolean voiceEnabled = (Boolean) userPrivileges.get("voiceEnabled");
            if (voiceEnabled == null || !voiceEnabled) {
                throw new RuntimeException("您的会员等级暂不支持语音聊天功能，请升级会员");
            }

            // 检查每日聊天限制（语音聊天也算在聊天次数内）
            Integer dailyChatLimit = (Integer) userPrivileges.get("dailyChatLimit");
            if (dailyChatLimit != null && dailyChatLimit != -1) {
                // 检查用户今日的聊天次数是否达到限制
                if (sysUserDailyUsageService.hasReachedDailyChatLimit(currentUserId, dailyChatLimit)) {
                    throw new RuntimeException("您今日的聊天次数已达上限（" + dailyChatLimit + "次），请明日再试或升级会员");
                }
            }

            Logger.info("用户 {} 语音聊天权限检查通过，每日限制: {}", currentUserId, dailyChatLimit);

        } catch (Exception e) {
            Logger.error("检查用户语音聊天权限失败: {}", e.getMessage(), e);
            throw new RuntimeException("权限检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户权限信息
     *
     * @param userId 用户ID
     * @return 权限信息
     */
    private Map<String, Object> getUserPrivileges(Long userId) {
        try {
            // 查询用户会员信息
            SysUserVip userVip = sysUserVipService.selectValidVipByUserId(userId);

            Integer vipLevel = 0;
            if (userVip != null && "1".equals(userVip.getVipStatus())) {
                vipLevel = userVip.getVipLevel();
            }

            // 根据会员等级获取权益信息（复用VipController中的逻辑）
            return getPrivilegesByLevel(vipLevel);

        } catch (Exception e) {
            Logger.error("获取用户权限信息失败: userId={}, error={}", userId, e.getMessage(), e);
            // 返回普通用户权限
            return getPrivilegesByLevel(0);
        }
    }

    /**
     * 根据会员等级获取权益信息
     *
     * @param level 会员等级
     * @return 权益信息
     */
    private Map<String, Object> getPrivilegesByLevel(Integer level) {
        Map<String, Object> privileges = new HashMap<>();

        switch (level == null ? 0 : level) {
            case 0: // 普通用户
                privileges.put("dailyChatLimit", 10);
                privileges.put("dailyImageLimit", 0);
                privileges.put("customRoleQuota", 0);
                privileges.put("voiceEnabled", false);
                privileges.put("imageEnabled", false);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", false);
                privileges.put("prioritySupportEnabled", false);
                break;
            case 1: // 白银会员
                privileges.put("dailyChatLimit", 50);
                privileges.put("dailyImageLimit", 0);
                privileges.put("customRoleQuota", 2);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", false);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", false);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 2: // 黄金会员
                privileges.put("dailyChatLimit", 150);
                privileges.put("dailyImageLimit", 10);
                privileges.put("customRoleQuota", 5);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 3: // 铂金会员
                privileges.put("dailyChatLimit", 500);
                privileges.put("dailyImageLimit", 30);
                privileges.put("customRoleQuota", 10);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", true);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 4: // 钻石会员
                privileges.put("dailyChatLimit", -1); // 无限
                privileges.put("dailyImageLimit", -1); // 无限
                privileges.put("customRoleQuota", -1); // 无限
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", true);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
        }

        return privileges;
    }
}
