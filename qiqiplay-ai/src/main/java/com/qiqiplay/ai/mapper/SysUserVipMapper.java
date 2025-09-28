package com.qiqiplay.ai.mapper;

import java.util.List;
import com.qiqiplay.ai.domain.SysUserVip;

/**
 * 会员订阅Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface SysUserVipMapper 
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
     * 删除会员订阅
     * 
     * @param vipId 会员订阅主键
     * @return 结果
     */
    public int deleteSysUserVipByVipId(Long vipId);

    /**
     * 批量删除会员订阅
     * 
     * @param vipIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysUserVipByVipIds(Long[] vipIds);
}
