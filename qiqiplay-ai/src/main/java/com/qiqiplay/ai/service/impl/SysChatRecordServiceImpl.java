package com.qiqiplay.ai.service.impl;

import java.util.List;
import com.qiqiplay.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysChatRecordMapper;
import com.qiqiplay.ai.domain.SysChatRecord;
import com.qiqiplay.ai.service.ISysChatRecordService;

/**
 * 聊天记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysChatRecordServiceImpl implements ISysChatRecordService 
{
    @Autowired
    private SysChatRecordMapper sysChatRecordMapper;

    /**
     * 查询聊天记录
     * 
     * @param recordId 聊天记录主键
     * @return 聊天记录
     */
    @Override
    public SysChatRecord selectSysChatRecordByRecordId(Long recordId)
    {
        return sysChatRecordMapper.selectSysChatRecordByRecordId(recordId);
    }

    /**
     * 查询聊天记录列表
     * 
     * @param sysChatRecord 聊天记录
     * @return 聊天记录
     */
    @Override
    public List<SysChatRecord> selectSysChatRecordList(SysChatRecord sysChatRecord)
    {
        return sysChatRecordMapper.selectSysChatRecordList(sysChatRecord);
    }

    /**
     * 新增聊天记录
     * 
     * @param sysChatRecord 聊天记录
     * @return 结果
     */
    @Override
    public int insertSysChatRecord(SysChatRecord sysChatRecord)
    {
        sysChatRecord.setCreateTime(DateUtils.getNowDate());
        return sysChatRecordMapper.insertSysChatRecord(sysChatRecord);
    }

    /**
     * 修改聊天记录
     * 
     * @param sysChatRecord 聊天记录
     * @return 结果
     */
    @Override
    public int updateSysChatRecord(SysChatRecord sysChatRecord)
    {
        return sysChatRecordMapper.updateSysChatRecord(sysChatRecord);
    }

    /**
     * 批量删除聊天记录
     * 
     * @param recordIds 需要删除的聊天记录主键
     * @return 结果
     */
    @Override
    public int deleteSysChatRecordByRecordIds(Long[] recordIds)
    {
        return sysChatRecordMapper.deleteSysChatRecordByRecordIds(recordIds);
    }

    /**
     * 删除聊天记录信息
     * 
     * @param recordId 聊天记录主键
     * @return 结果
     */
    @Override
    public int deleteSysChatRecordByRecordId(Long recordId)
    {
        return sysChatRecordMapper.deleteSysChatRecordByRecordId(recordId);
    }
}
