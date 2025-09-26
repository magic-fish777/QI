package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysRoleAi;

/**
 * AI角色信息Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysRoleAiService 
{
    /**
     * 查询AI角色信息
     * 
     * @param roleId AI角色信息主键
     * @return AI角色信息
     */
    public SysRoleAi selectSysRoleAiByRoleId(Long roleId);

    /**
     * 查询AI角色信息列表
     * 
     * @param sysRoleAi AI角色信息
     * @return AI角色信息集合
     */
    public List<SysRoleAi> selectSysRoleAiList(SysRoleAi sysRoleAi);

    /**
     * 新增AI角色信息
     * 
     * @param sysRoleAi AI角色信息
     * @return 结果
     */
    public int insertSysRoleAi(SysRoleAi sysRoleAi);

    /**
     * 修改AI角色信息
     * 
     * @param sysRoleAi AI角色信息
     * @return 结果
     */
    public int updateSysRoleAi(SysRoleAi sysRoleAi);

    /**
     * 批量删除AI角色信息
     * 
     * @param roleIds 需要删除的AI角色信息主键集合
     * @return 结果
     */
    public int deleteSysRoleAiByRoleIds(Long[] roleIds);

    /**
     * 删除AI角色信息信息
     * 
     * @param roleId AI角色信息主键
     * @return 结果
     */
    public int deleteSysRoleAiByRoleId(Long roleId);
}
