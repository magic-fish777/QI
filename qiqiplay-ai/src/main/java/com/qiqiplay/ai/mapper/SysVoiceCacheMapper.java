package com.qiqiplay.ai.mapper;

import java.util.List;
import com.qiqiplay.ai.domain.SysVoiceCache;

/**
 * 语音缓存Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface SysVoiceCacheMapper 
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
     * 删除语音缓存
     * 
     * @param voiceId 语音缓存主键
     * @return 结果
     */
    public int deleteSysVoiceCacheByVoiceId(Long voiceId);

    /**
     * 批量删除语音缓存
     * 
     * @param voiceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysVoiceCacheByVoiceIds(Long[] voiceIds);
}
