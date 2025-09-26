package com.qiqiplay.ai.mapper;

import java.util.List;
import com.qiqiplay.ai.domain.SysFrontUser;

/**
 * 前台用户信息（精简版）Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface SysFrontUserMapper 
{
    /**
     * 查询前台用户信息（精简版）
     * 
     * @param userId 前台用户信息（精简版）主键
     * @return 前台用户信息（精简版）
     */
    public SysFrontUser selectSysFrontUserByUserId(Long userId);

    /**
     * 查询前台用户信息（精简版）列表
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 前台用户信息（精简版）集合
     */
    public List<SysFrontUser> selectSysFrontUserList(SysFrontUser sysFrontUser);

    /**
     * 新增前台用户信息（精简版）
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    public int insertSysFrontUser(SysFrontUser sysFrontUser);

    /**
     * 修改前台用户信息（精简版）
     * 
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    public int updateSysFrontUser(SysFrontUser sysFrontUser);

    /**
     * 删除前台用户信息（精简版）
     * 
     * @param userId 前台用户信息（精简版）主键
     * @return 结果
     */
    public int deleteSysFrontUserByUserId(Long userId);

    /**
     * 批量删除前台用户信息（精简版）
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFrontUserByUserIds(Long[] userIds);
}
