package com.qiqiplay.ai.mapper;

import java.util.List;
import com.qiqiplay.ai.domain.SysRoleAi;

/**
 * AI角色信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface SysRoleAiMapper 
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
     * 删除AI角色信息
     * 
     * @param roleId AI角色信息主键
     * @return 结果
     */
    public int deleteSysRoleAiByRoleId(Long roleId);

    /**
     * 批量删除AI角色信息
     * 
     * @param roleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysRoleAiByRoleIds(Long[] roleIds);
}
