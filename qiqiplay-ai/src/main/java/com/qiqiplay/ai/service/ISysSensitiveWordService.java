package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysSensitiveWord;

/**
 * 敏感词库Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysSensitiveWordService 
{
    /**
     * 查询敏感词库
     * 
     * @param wordId 敏感词库主键
     * @return 敏感词库
     */
    public SysSensitiveWord selectSysSensitiveWordByWordId(Long wordId);

    /**
     * 查询敏感词库列表
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 敏感词库集合
     */
    public List<SysSensitiveWord> selectSysSensitiveWordList(SysSensitiveWord sysSensitiveWord);

    /**
     * 新增敏感词库
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 结果
     */
    public int insertSysSensitiveWord(SysSensitiveWord sysSensitiveWord);

    /**
     * 修改敏感词库
     * 
     * @param sysSensitiveWord 敏感词库
     * @return 结果
     */
    public int updateSysSensitiveWord(SysSensitiveWord sysSensitiveWord);

    /**
     * 批量删除敏感词库
     * 
     * @param wordIds 需要删除的敏感词库主键集合
     * @return 结果
     */
    public int deleteSysSensitiveWordByWordIds(Long[] wordIds);

    /**
     * 删除敏感词库信息
     * 
     * @param wordId 敏感词库主键
     * @return 结果
     */
    public int deleteSysSensitiveWordByWordId(Long wordId);
}
