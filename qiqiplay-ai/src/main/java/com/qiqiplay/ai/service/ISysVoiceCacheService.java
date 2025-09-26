package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysVoiceCache;

/**
 * 语音缓存Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysVoiceCacheService 
{
    /**
     * 查询语音缓存
     * 
     * @param voiceId 语音缓存主键
     * @return 语音缓存
     */
    public SysVoiceCache selectSysVoiceCacheByVoiceId(Long voiceId);

    /**
     * 查询语音缓存列表
     * 
     * @param sysVoiceCache 语音缓存
     * @return 语音缓存集合
     */
    public List<SysVoiceCache> selectSysVoiceCacheList(SysVoiceCache sysVoiceCache);

    /**
     * 新增语音缓存
     * 
     * @param sysVoiceCache 语音缓存
     * @return 结果
     */
    public int insertSysVoiceCache(SysVoiceCache sysVoiceCache);

    /**
     * 修改语音缓存
     * 
     * @param sysVoiceCache 语音缓存
     * @return 结果
     */
    public int updateSysVoiceCache(SysVoiceCache sysVoiceCache);

    /**
     * 批量删除语音缓存
     * 
     * @param voiceIds 需要删除的语音缓存主键集合
     * @return 结果
     */
    public int deleteSysVoiceCacheByVoiceIds(Long[] voiceIds);

    /**
     * 删除语音缓存信息
     * 
     * @param voiceId 语音缓存主键
     * @return 结果
     */
    public int deleteSysVoiceCacheByVoiceId(Long voiceId);
}
