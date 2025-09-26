package com.qiqiplay.ai.service.impl;

import java.util.List;
import com.qiqiplay.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysFrontUserMapper;
import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.service.ISysFrontUserService;

/**
 * 前台用户信息（精简版）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysFrontUserServiceImpl implements ISysFrontUserService 
{
    @Autowired
    private SysFrontUserMapper sysFrontUserMapper;

    /**
     * 查询前台用户信息（精简版）
     * 
     * @param userId 前台用户信息（精简版）主键
     * @return 前台用户信息（精简版）
     */
    @Override
    public SysFrontUser selectSysFrontUserByUserId(Long userId)
    {
        return sysFrontUserMapper.selectSysFrontUserByUserId(userId);
    }

    /**
     * 查询前台用户信息（精简版）列表
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 前台用户信息（精简版）
     */
    @Override
    public List<SysFrontUser> selectSysFrontUserList(SysFrontUser sysFrontUser)
    {
        return sysFrontUserMapper.selectSysFrontUserList(sysFrontUser);
    }

    /**
     * 新增前台用户信息（精简版）
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    @Override
    public int insertSysFrontUser(SysFrontUser sysFrontUser)
    {
        sysFrontUser.setCreateTime(DateUtils.getNowDate());
        return sysFrontUserMapper.insertSysFrontUser(sysFrontUser);
    }

    /**
     * 修改前台用户信息（精简版）
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    @Override
    public int updateSysFrontUser(SysFrontUser sysFrontUser)
    {
        sysFrontUser.setUpdateTime(DateUtils.getNowDate());
        return sysFrontUserMapper.updateSysFrontUser(sysFrontUser);
    }

    /**
     * 批量删除前台用户信息（精简版）
     * 
     * @param userIds 需要删除的前台用户信息（精简版）主键
     * @return 结果
     */
    @Override
    public int deleteSysFrontUserByUserIds(Long[] userIds)
    {
        return sysFrontUserMapper.deleteSysFrontUserByUserIds(userIds);
    }

    /**
     * 删除前台用户信息（精简版）信息
     * 
     * @param userId 前台用户信息（精简版）主键
     * @return 结果
     */
    @Override
    public int deleteSysFrontUserByUserId(Long userId)
    {
        return sysFrontUserMapper.deleteSysFrontUserByUserId(userId);
    }
}
