package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysUserVip;

/**
 * 会员订阅Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysUserVipService 
{
    /**
     * 查询会员订阅
     * 
     * @param vipId 会员订阅主键
     * @return 会员订阅
     */
    public SysUserVip selectSysUserVipByVipId(Long vipId);

    /**
     * 查询会员订阅列表
     * 
     * @param sysUserVip 会员订阅
     * @return 会员订阅集合
     */
    public List<SysUserVip> selectSysUserVipList(SysUserVip sysUserVip);

    /**
     * 新增会员订阅
     * 
     * @param sysUserVip 会员订阅
     * @return 结果
     */
    public int insertSysUserVip(SysUserVip sysUserVip);

    /**
     * 修改会员订阅
     * 
     * @param sysUserVip 会员订阅
     * @return 结果
     */
    public int updateSysUserVip(SysUserVip sysUserVip);

    /**
     * 批量删除会员订阅
     * 
     * @param vipIds 需要删除的会员订阅主键集合
     * @return 结果
     */
    public int deleteSysUserVipByVipIds(Long[] vipIds);

    /**
     * 删除会员订阅信息
     *
     * @param vipId 会员订阅主键
     * @return 结果
     */
    public int deleteSysUserVipByVipId(Long vipId);

    /**
     * 根据用户ID查询有效的会员信息
     *
     * @param userId 用户ID
     * @return 会员信息
     */
    public SysUserVip selectValidVipByUserId(Long userId);
}
