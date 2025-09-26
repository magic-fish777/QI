package com.qiqiplay.ai.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysVoiceCacheMapper;
import com.qiqiplay.ai.domain.SysVoiceCache;
import com.qiqiplay.ai.service.ISysVoiceCacheService;

/**
 * 语音缓存Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysVoiceCacheServiceImpl implements ISysVoiceCacheService 
{
    @Autowired
    private SysVoiceCacheMapper sysVoiceCacheMapper;

    /**
     * 查询语音缓存
     * 
     * @param voiceId 语音缓存主键
     * @return 语音缓存
     */
    @Override
    public SysVoiceCache selectSysVoiceCacheByVoiceId(Long voiceId)
    {
        return sysVoiceCacheMapper.selectSysVoiceCacheByVoiceId(voiceId);
    }

    /**
     * 查询语音缓存列表
     * 
     * @param sysVoiceCache 语音缓存
     * @return 语音缓存
     */
    @Override
    public List<SysVoiceCache> selectSysVoiceCacheList(SysVoiceCache sysVoiceCache)
    {
        return sysVoiceCacheMapper.selectSysVoiceCacheList(sysVoiceCache);
    }

    /**
     * 新增语音缓存
     * 
     * @param sysVoiceCache 语音缓存
     * @return 结果
     */
    @Override
    public int insertSysVoiceCache(SysVoiceCache sysVoiceCache)
    {
        return sysVoiceCacheMapper.insertSysVoiceCache(sysVoiceCache);
    }

    /**
     * 修改语音缓存
     * 
     * @param sysVoiceCache 语音缓存
     * @return 结果
     */
    @Override
    public int updateSysVoiceCache(SysVoiceCache sysVoiceCache)
    {
        return sysVoiceCacheMapper.updateSysVoiceCache(sysVoiceCache);
    }

    /**
     * 批量删除语音缓存
     * 
     * @param voiceIds 需要删除的语音缓存主键
     * @return 结果
     */
    @Override
    public int deleteSysVoiceCacheByVoiceIds(Long[] voiceIds)
    {
        return sysVoiceCacheMapper.deleteSysVoiceCacheByVoiceIds(voiceIds);
    }

    /**
     * 删除语音缓存信息
     * 
     * @param voiceId 语音缓存主键
     * @return 结果
     */
    @Override
    public int deleteSysVoiceCacheByVoiceId(Long voiceId)
    {
        return sysVoiceCacheMapper.deleteSysVoiceCacheByVoiceId(voiceId);
    }
}
