package com.qiqiplay.ai.service.impl;

import java.util.List;
import com.qiqiplay.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysRoleAiMapper;
import com.qiqiplay.ai.domain.SysRoleAi;
import com.qiqiplay.ai.service.ISysRoleAiService;

/**
 * AI角色信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysRoleAiServiceImpl implements ISysRoleAiService 
{
    @Autowired
    private SysRoleAiMapper sysRoleAiMapper;

    /**
     * 查询AI角色信息
     * 
     * @param roleId AI角色信息主键
     * @return AI角色信息
     */
    @Override
    public SysRoleAi selectSysRoleAiByRoleId(Long roleId)
    {
        return sysRoleAiMapper.selectSysRoleAiByRoleId(roleId);
    }

    /**
     * 查询AI角色信息列表
     * 
     * @param sysRoleAi AI角色信息
     * @return AI角色信息
     */
    @Override
    public List<SysRoleAi> selectSysRoleAiList(SysRoleAi sysRoleAi)
    {
        return sysRoleAiMapper.selectSysRoleAiList(sysRoleAi);
    }

    /**
     * 新增AI角色信息
     * 
     * @param sysRoleAi AI角色信息
     * @return 结果
     */
    @Override
    public int insertSysRoleAi(SysRoleAi sysRoleAi)
    {
        sysRoleAi.setCreateTime(DateUtils.getNowDate());
        return sysRoleAiMapper.insertSysRoleAi(sysRoleAi);
    }

    /**
     * 修改AI角色信息
     * 
     * @param sysRoleAi AI角色信息
     * @return 结果
     */
    @Override
    public int updateSysRoleAi(SysRoleAi sysRoleAi)
    {
        sysRoleAi.setUpdateTime(DateUtils.getNowDate());
        return sysRoleAiMapper.updateSysRoleAi(sysRoleAi);
    }

    /**
     * 批量删除AI角色信息
     * 
     * @param roleIds 需要删除的AI角色信息主键
     * @return 结果
     */
    @Override
    public int deleteSysRoleAiByRoleIds(Long[] roleIds)
    {
        return sysRoleAiMapper.deleteSysRoleAiByRoleIds(roleIds);
    }

    /**
     * 删除AI角色信息信息
     * 
     * @param roleId AI角色信息主键
     * @return 结果
     */
    @Override
    public int deleteSysRoleAiByRoleId(Long roleId)
    {
        return sysRoleAiMapper.deleteSysRoleAiByRoleId(roleId);
    }
}
