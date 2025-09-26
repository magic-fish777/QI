package com.qiqiplay.ai.service.impl;

import java.util.List;
import com.qiqiplay.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysSensitiveWordMapper;
import com.qiqiplay.ai.domain.SysSensitiveWord;
import com.qiqiplay.ai.service.ISysSensitiveWordService;

/**
 * 敏感词库Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysSensitiveWordServiceImpl implements ISysSensitiveWordService 
{
    @Autowired
    private SysSensitiveWordMapper sysSensitiveWordMapper;

    /**
     * 查询敏感词库
     * 
     * @param wordId 敏感词库主键
     * @return 敏感词库
     */
    @Override
    public SysSensitiveWord selectSysSensitiveWordByWordId(Long wordId)
    {
        return sysSensitiveWordMapper.selectSysSensitiveWordByWordId(wordId);
    }

    /**
     * 查询敏感词库列表
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 敏感词库
     */
    @Override
    public List<SysSensitiveWord> selectSysSensitiveWordList(SysSensitiveWord sysSensitiveWord)
    {
        return sysSensitiveWordMapper.selectSysSensitiveWordList(sysSensitiveWord);
    }

    /**
     * 新增敏感词库
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 结果
     */
    @Override
    public int insertSysSensitiveWord(SysSensitiveWord sysSensitiveWord)
    {
        sysSensitiveWord.setCreateTime(DateUtils.getNowDate());
        return sysSensitiveWordMapper.insertSysSensitiveWord(sysSensitiveWord);
    }

    /**
     * 修改敏感词库
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 结果
     */
    @Override
    public int updateSysSensitiveWord(SysSensitiveWord sysSensitiveWord)
    {
        sysSensitiveWord.setUpdateTime(DateUtils.getNowDate());
        return sysSensitiveWordMapper.updateSysSensitiveWord(sysSensitiveWord);
    }

    /**
     * 批量删除敏感词库
     * 
     * @param wordIds 需要删除的敏感词库主键
     * @return 结果
     */
    @Override
    public int deleteSysSensitiveWordByWordIds(Long[] wordIds)
    {
        return sysSensitiveWordMapper.deleteSysSensitiveWordByWordIds(wordIds);
    }

    /**
     * 删除敏感词库信息
     * 
     * @param wordId 敏感词库主键
     * @return 结果
     */
    @Override
    public int deleteSysSensitiveWordByWordId(Long wordId)
    {
        return sysSensitiveWordMapper.deleteSysSensitiveWordByWordId(wordId);
    }
}
