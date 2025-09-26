package com.qiqiplay.ai.mapper;

import java.util.List;
import com.qiqiplay.ai.domain.SysSensitiveWord;

/**
 * 敏感词库Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface SysSensitiveWordMapper 
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
     * 删除敏感词库
     * 
     * @param wordId 敏感词库主键
     * @return 结果
     */
    public int deleteSysSensitiveWordByWordId(Long wordId);

    /**
     * 批量删除敏感词库
     * 
     * @param wordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSensitiveWordByWordIds(Long[] wordIds);
}
