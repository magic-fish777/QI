package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysChatRecord;

/**
 * 聊天记录Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysChatRecordService 
{
    /**
     * 查询聊天记录
     * 
     * @param recordId 聊天记录主键
     * @return 聊天记录
     */
    public SysChatRecord selectSysChatRecordByRecordId(Long recordId);

    /**
     * 查询聊天记录列表
     * 
     * @param sysChatRecord 聊天记录
     * @return 聊天记录集合
     */
    public List<SysChatRecord> selectSysChatRecordList(SysChatRecord sysChatRecord);

    /**
     * 新增聊天记录
     * 
     * @param sysChatRecord 聊天记录
     * @return 结果
     */
    public int insertSysChatRecord(SysChatRecord sysChatRecord);

    /**
     * 修改聊天记录
     * 
     * @param sysChatRecord 聊天记录
     * @return 结果
     */
    public int updateSysChatRecord(SysChatRecord sysChatRecord);

    /**
     * 批量删除聊天记录
     * 
     * @param recordIds 需要删除的聊天记录主键集合
     * @return 结果
     */
    public int deleteSysChatRecordByRecordIds(Long[] recordIds);

    /**
     * 删除聊天记录信息
     * 
     * @param recordId 聊天记录主键
     * @return 结果
     */
    public int deleteSysChatRecordByRecordId(Long recordId);
}
