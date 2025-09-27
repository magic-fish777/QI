package com.qiqiplay.web.controller.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Base64;
import com.qiqiplay.web.controller.chat.domain.TextChatRequest;
import com.qiqiplay.web.controller.chat.domain.AudioChatRequest;
import com.qiqiplay.web.controller.chat.domain.ChatResponse;
import com.qiqiplay.web.controller.chat.service.IChatService;

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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ChatResponse processTextChat(TextChatRequest request)
    {
        try
        {
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

            return parseResponse(response.getBody());
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
            // 将base64编码的音频数据解码为字节数组
            byte[] audioBytes = Base64.getDecoder().decode(request.getAudio());

            // 创建ByteArrayResource来包装音频数据
            ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
                @Override
                public String getFilename() {
                    return "audio.wav"; // 提供文件名
                }
            };

            // 构建multipart/form-data请求
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("audio", audioResource);
            parts.add("role", request.getRole());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parts, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                AUDIO_CHAT_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            return parseResponse(response.getBody());
        }
        catch (Exception e)
        {
            throw new RuntimeException("调用语音聊天接口失败", e);
        }
    }

    private ChatResponse parseResponse(String responseBody) throws Exception
    {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setRole(jsonNode.path("role").asText());
        chatResponse.setUser(jsonNode.path("user").asText());
        chatResponse.setReply(jsonNode.path("reply").asText());
        chatResponse.setAudioFile(jsonNode.path("audioFile").asText());

        return chatResponse;
    }
}