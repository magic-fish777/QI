package com.qiqiplay.web.controller.chat.listener;

import com.qiqiplay.ai.domain.SysChatRecord;
import com.qiqiplay.ai.domain.SysVoiceCache;
import com.qiqiplay.ai.service.ISysChatRecordService;
import com.qiqiplay.ai.service.ISysVoiceCacheService;
import com.qiqiplay.web.controller.chat.event.ChatRecordEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Calendar;

/**
 * 聊天记录事件监听器
 * 异步处理聊天记录的保存，避免阻塞主线程
 *
 * @author qiqiplay
 */
@Component
public class ChatRecordEventListener {

    private static final Logger log = LoggerFactory.getLogger(ChatRecordEventListener.class);

    @Autowired
    private ISysChatRecordService sysChatRecordService;

    @Autowired
    private ISysVoiceCacheService sysVoiceCacheService;

    /**
     * 异步处理聊天记录保存事件
     *
     * @param event 聊天记录事件
     */
    @Async("chatRecordExecutor")
    @EventListener
    public void handleChatRecordEvent(ChatRecordEvent event) {
        try {
            log.info("开始异步保存聊天记录: userId={}, roleId={}, inputType={}",
                    event.getUserId(), event.getRoleId(), event.getUserInputType());

            // 创建聊天记录对象
            SysChatRecord chatRecord = new SysChatRecord();
            chatRecord.setUserId(event.getUserId());
            chatRecord.setRoleId(event.getRoleId());
            chatRecord.setUserInputType(event.getUserInputType());
            chatRecord.setUserInputContent(event.getUserInputContent());
            chatRecord.setAiReplyContent(event.getAiReplyContent());
            chatRecord.setChatLanguage(event.getChatLanguage());
            chatRecord.setChatTime(new Date());
            chatRecord.setCreateTime(new Date());

            // 设置必要的默认值
            chatRecord.setSkillId(1L); // 设置默认技能ID，可以根据实际业务调整
            chatRecord.setDelFlag("0"); // 设置删除标志为正常状态

            // 异步保存聊天记录
            int result = sysChatRecordService.insertSysChatRecord(chatRecord);

            if (result > 0) {
                log.info("聊天记录保存成功: recordId={}, userId={}, roleId={}",
                        chatRecord.getRecordId(), event.getUserId(), event.getRoleId());

                // 保存语音记录
                saveVoiceRecords(chatRecord.getRecordId(), event);
            } else {
                log.warn("聊天记录保存失败: userId={}, roleId={}", event.getUserId(), event.getRoleId());
            }

        } catch (Exception e) {
            log.error("异步保存聊天记录时发生异常: userId={}, roleId={}, error={}",
                    event.getUserId(), event.getRoleId(), e.getMessage(), e);
        }
    }

    /**
     * 保存语音记录
     *
     * @param recordId 聊天记录ID
     * @param event 聊天记录事件
     */
    private void saveVoiceRecords(Long recordId, ChatRecordEvent event) {
        try {
            // 保存用户输入的语音文件（如果存在）
            if (event.getUserAudioFileUrl() != null && !event.getUserAudioFileUrl().trim().isEmpty()) {
                SysVoiceCache userVoiceCache = new SysVoiceCache();
                userVoiceCache.setRecordId(recordId);
                userVoiceCache.setVoiceType("1"); // 1-用户语音
                userVoiceCache.setRelatedRecordId(recordId); // 设置关联记录ID
                userVoiceCache.setVoiceUrl(event.getUserAudioFileUrl());
                userVoiceCache.setVoiceDuration(0L); // 设置默认时长（秒）
                userVoiceCache.setUploadTime(new Date());

                // 设置过期时间（30天后过期）
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                userVoiceCache.setExpireTime(calendar.getTime());

                userVoiceCache.setDelFlag("0");

                int userResult = sysVoiceCacheService.insertSysVoiceCache(userVoiceCache);
                if (userResult > 0) {
                    log.info("用户语音记录保存成功: recordId={}, voiceUrl={}", recordId, event.getUserAudioFileUrl());
                } else {
                    log.warn("用户语音记录保存失败: recordId={}, voiceUrl={}", recordId, event.getUserAudioFileUrl());
                }
            }

            // 保存AI回复的语音文件（如果存在）
            if (event.getAiAudioFileUrl() != null && !event.getAiAudioFileUrl().trim().isEmpty()) {
                SysVoiceCache aiVoiceCache = new SysVoiceCache();
                aiVoiceCache.setRecordId(recordId);
                aiVoiceCache.setVoiceType("2"); // 2-AI语音
                aiVoiceCache.setRelatedRecordId(recordId); // 设置关联记录ID
                aiVoiceCache.setVoiceUrl(event.getAiAudioFileUrl());
                aiVoiceCache.setVoiceDuration(0L); // 设置默认时长（秒）
                aiVoiceCache.setUploadTime(new Date());

                // 设置过期时间（30天后过期）
                Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.DAY_OF_MONTH, 30);
                aiVoiceCache.setExpireTime(calendar2.getTime());

                aiVoiceCache.setDelFlag("0");

                int aiResult = sysVoiceCacheService.insertSysVoiceCache(aiVoiceCache);
                if (aiResult > 0) {
                    log.info("AI语音记录保存成功: recordId={}, voiceUrl={}", recordId, event.getAiAudioFileUrl());
                } else {
                    log.warn("AI语音记录保存失败: recordId={}, voiceUrl={}", recordId, event.getAiAudioFileUrl());
                }
            }

        } catch (Exception e) {
            log.error("保存语音记录时发生异常: recordId={}, error={}", recordId, e.getMessage(), e);
        }
    }
}