package com.qiqiplay.ai.service.impl;

import java.util.List;
import com.qiqiplay.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysUserVipMapper;
import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.ai.service.ISysUserVipService;

/**
 * 会员订阅Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysUserVipServiceImpl implements ISysUserVipService 
{
    @Autowired
    private SysUserVipMapper sysUserVipMapper;

    /**
     * 查询会员订阅
     * 
     * @param vipId 会员订阅主键
     * @return 会员订阅
     */
    @Override
    public SysUserVip selectSysUserVipByVipId(Long vipId)
    {
        return sysUserVipMapper.selectSysUserVipByVipId(vipId);
    }

    /**
     * 查询会员订阅列表
     * 
     * @param sysUserVip 会员订阅
     * @return 会员订阅
     */
    @Override
    public List<SysUserVip> selectSysUserVipList(SysUserVip sysUserVip)
    {
        return sysUserVipMapper.selectSysUserVipList(sysUserVip);
    }

    /**
     * 新增会员订阅
     * 
     * @param sysUserVip 会员订阅
     * @return 结果
     */
    @Override
    public int insertSysUserVip(SysUserVip sysUserVip)
    {
        sysUserVip.setCreateTime(DateUtils.getNowDate());
        return sysUserVipMapper.insertSysUserVip(sysUserVip);
    }

    /**
     * 修改会员订阅
     * 
     * @param sysUserVip 会员订阅
     * @return 结果
     */
    @Override
    public int updateSysUserVip(SysUserVip sysUserVip)
    {
        sysUserVip.setUpdateTime(DateUtils.getNowDate());
        return sysUserVipMapper.updateSysUserVip(sysUserVip);
    }

    /**
     * 批量删除会员订阅
     * 
     * @param vipIds 需要删除的会员订阅主键
     * @return 结果
     */
    @Override
    public int deleteSysUserVipByVipIds(Long[] vipIds)
    {
        return sysUserVipMapper.deleteSysUserVipByVipIds(vipIds);
    }

    /**
     * 删除会员订阅信息
     *
     * @param vipId 会员订阅主键
     * @return 结果
     */
    @Override
    public int deleteSysUserVipByVipId(Long vipId)
    {
        return sysUserVipMapper.deleteSysUserVipByVipId(vipId);
    }

    /**
     * 根据用户ID查询有效的会员信息
     *
     * @param userId 用户ID
     * @return 会员信息
     */
    @Override
    public SysUserVip selectValidVipByUserId(Long userId)
    {
        return sysUserVipMapper.selectValidVipByUserId(userId);
    }
}
