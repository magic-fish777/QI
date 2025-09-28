package com.qiqiplay.ai.mapper;

import java.util.List;
import java.util.Date;
import com.qiqiplay.ai.domain.SysUserDailyUsage;
import org.apache.ibatis.annotations.Param;

/**
 * 用户每日使用统计Mapper接口
 *
 * @author qiqiplay
 * @date 2024-09-28
 */
public interface SysUserDailyUsageMapper
{
    /**
     * 查询用户每日使用统计
     *
     * @param usageId 用户每日使用统计主键
     * @return 用户每日使用统计
     */
    public SysUserDailyUsage selectSysUserDailyUsageByUsageId(Long usageId);

    /**
     * 查询用户每日使用统计列表
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 用户每日使用统计集合
     */
    public List<SysUserDailyUsage> selectSysUserDailyUsageList(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 新增用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    public int insertSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 修改用户每日使用统计
     *
     * @param sysUserDailyUsage 用户每日使用统计
     * @return 结果
     */
    public int updateSysUserDailyUsage(SysUserDailyUsage sysUserDailyUsage);

    /**
     * 删除用户每日使用统计
     *
     * @param usageId 用户每日使用统计主键
     * @return 结果
     */
    public int deleteSysUserDailyUsageByUsageId(Long usageId);

    /**
     * 批量删除用户每日使用统计
     *
     * @param usageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysUserDailyUsageByUsageIds(Long[] usageIds);

    /**
     * 根据用户ID和日期查询使用统计
     *
     * @param userId 用户ID
     * @param usageDate 使用日期
     * @return 使用统计
     */
    public SysUserDailyUsage selectByUserIdAndDate(@Param("userId") Long userId, @Param("usageDate") Date usageDate);

    /**
     * 增加聊天使用次数
     *
     * @param userId 用户ID
     * @param usageDate 使用日期
     * @return 结果
     */
    public int incrementChatCount(@Param("userId") Long userId, @Param("usageDate") Date usageDate);

    /**
     * 增加图片生成使用次数
     *
     * @param userId 用户ID
     * @param usageDate 使用日期
     * @return 结果
     */
    public int incrementImageCount(@Param("userId") Long userId, @Param("usageDate") Date usageDate);

    /**
     * 增加语音使用次数
     *
     * @param userId 用户ID
     * @param usageDate 使用日期
     * @return 结果
     */
    public int incrementVoiceCount(@Param("userId") Long userId, @Param("usageDate") Date usageDate);
}