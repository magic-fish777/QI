package com.qiqiplay.ai.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.qiqiplay.common.annotation.Log;
import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.enums.BusinessType;
import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.ai.service.ISysUserVipService;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import com.qiqiplay.common.core.page.TableDataInfo;

/**
 * 会员订阅Controller
 *
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/ai/vip")
public class SysUserVipController extends BaseController
{
    @Autowired
    private ISysUserVipService sysUserVipService;

    /**
     * 查询会员订阅列表
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUserVip sysUserVip)
    {
        startPage();
        List<SysUserVip> list = sysUserVipService.selectSysUserVipList(sysUserVip);
        return getDataTable(list);
    }

    /**
     * 导出会员订阅列表
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:export')")
    @Log(title = "会员订阅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserVip sysUserVip)
    {
        List<SysUserVip> list = sysUserVipService.selectSysUserVipList(sysUserVip);
        ExcelUtil<SysUserVip> util = new ExcelUtil<SysUserVip>(SysUserVip.class);
        util.exportExcel(response, list, "会员订阅数据");
    }

    /**
     * 获取会员订阅详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:query')")
    @GetMapping(value = "/{vipId}")
    public AjaxResult getInfo(@PathVariable("vipId") Long vipId)
    {
        return success(sysUserVipService.selectSysUserVipByVipId(vipId));
    }

    /**
     * 新增会员订阅
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:add')")
    @Log(title = "会员订阅", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysUserVip sysUserVip)
    {
        return toAjax(sysUserVipService.insertSysUserVip(sysUserVip));
    }

    /**
     * 修改会员订阅
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:edit')")
    @Log(title = "会员订阅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysUserVip sysUserVip)
    {
        return toAjax(sysUserVipService.updateSysUserVip(sysUserVip));
    }

    /**
     * 删除会员订阅
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:remove')")
    @Log(title = "会员订阅", businessType = BusinessType.DELETE)
	@DeleteMapping("/{vipIds}")
    public AjaxResult remove(@PathVariable Long[] vipIds)
    {
        return toAjax(sysUserVipService.deleteSysUserVipByVipIds(vipIds));
    }

    /**
     * 获取会员统计数据
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:list')")
    @GetMapping("/statistics")
    public AjaxResult getVipStatistics()
    {
        try
        {
            Map<String, Object> statistics = new HashMap<>();

            // 查询所有会员记录
            List<SysUserVip> allVips = sysUserVipService.selectSysUserVipList(new SysUserVip());

            // 统计各等级会员数量
            Map<String, Integer> levelCounts = new HashMap<>();
            levelCounts.put("total", allVips.size());
            levelCounts.put("active", 0);
            levelCounts.put("expired", 0);

            // 按等级统计
            for (int i = 1; i <= 4; i++) {
                levelCounts.put("level" + i, 0);
            }

            Date now = new Date();
            for (SysUserVip vip : allVips) {
                // 统计活跃/过期会员
                if ("1".equals(vip.getVipStatus()) && vip.getExpireTime() != null && vip.getExpireTime().after(now)) {
                    levelCounts.put("active", levelCounts.get("active") + 1);

                    // 按等级统计
                    if (vip.getVipLevel() != null && vip.getVipLevel() >= 1 && vip.getVipLevel() <= 4) {
                        String levelKey = "level" + vip.getVipLevel();
                        levelCounts.put(levelKey, levelCounts.get(levelKey) + 1);
                    }
                } else {
                    levelCounts.put("expired", levelCounts.get("expired") + 1);
                }
            }

            statistics.put("memberCounts", levelCounts);

            // 模拟收入统计（实际应该从订单表统计）
            Map<String, Object> revenue = new HashMap<>();
            revenue.put("thisMonth", 12800.50);
            revenue.put("lastMonth", 9650.00);
            revenue.put("thisYear", 156780.00);
            statistics.put("revenue", revenue);

            // 最近7天新增会员趋势（模拟数据）
            List<Map<String, Object>> trends = new java.util.ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                Map<String, Object> trend = new HashMap<>();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -i);
                trend.put("date", cal.getTime());
                trend.put("newMembers", (int)(Math.random() * 20) + 5);
                trends.add(trend);
            }
            statistics.put("trends", trends);

            return success(statistics);
        }
        catch (Exception e)
        {
            logger.error("获取会员统计数据失败", e);
            return error("获取统计数据失败");
        }
    }

    /**
     * 手动开通/续费会员
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:edit')")
    @Log(title = "手动开通会员", businessType = BusinessType.UPDATE)
    @PostMapping("/manual-activate")
    public AjaxResult manualActivateVip(@RequestBody Map<String, Object> params)
    {
        try
        {
            Long userId = Long.valueOf(params.get("userId").toString());
            Integer vipLevel = Integer.valueOf(params.get("vipLevel").toString());
            Integer months = Integer.valueOf(params.get("months").toString());
            String reason = (String) params.get("reason");

            if (userId == null || vipLevel == null || months == null) {
                return error("参数不完整");
            }

            if (vipLevel < 1 || vipLevel > 4) {
                return error("VIP等级无效");
            }

            if (months < 1 || months > 120) {
                return error("月数范围应在1-120之间");
            }

            // 查询用户当前VIP状态
            SysUserVip currentVip = sysUserVipService.selectValidVipByUserId(userId);

            Date newExpireTime;
            if (currentVip != null && "1".equals(currentVip.getVipStatus()) &&
                currentVip.getExpireTime() != null && currentVip.getExpireTime().after(new Date())) {
                // 用户已有有效VIP，在当前到期时间基础上延长
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentVip.getExpireTime());
                calendar.add(Calendar.MONTH, months);
                newExpireTime = calendar.getTime();

                // 更新现有VIP记录
                currentVip.setVipLevel(vipLevel);
                currentVip.setExpireTime(newExpireTime);
                currentVip.setUpdateTime(new Date());
                currentVip.setRemark("管理员手动操作：" + (reason != null ? reason : "无"));

                sysUserVipService.updateSysUserVip(currentVip);
            } else {
                // 新开通VIP
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, months);
                newExpireTime = calendar.getTime();

                SysUserVip newVip = new SysUserVip();
                newVip.setUserId(userId);
                newVip.setVipLevel(vipLevel);
                newVip.setVipType("1"); // 默认类型
                newVip.setSubscribeTime(new Date());
                newVip.setExpireTime(newExpireTime);
                newVip.setVipStatus("1"); // 有效
                Integer quota = getCustomRoleQuota(vipLevel);
                newVip.setCustomRoleQuota(quota != null ? quota.longValue() : 0L);
                newVip.setCreateTime(new Date());
                newVip.setRemark("管理员手动开通：" + (reason != null ? reason : "无"));

                sysUserVipService.insertSysUserVip(newVip);
            }

            return success("会员开通/续费成功");
        }
        catch (Exception e)
        {
            logger.error("手动开通会员失败", e);
            return error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 批量延期会员
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:edit')")
    @Log(title = "批量延期会员", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-extend")
    public AjaxResult batchExtendVip(@RequestBody Map<String, Object> params)
    {
        try
        {
            Long[] vipIds = (Long[]) params.get("vipIds");
            Integer days = Integer.valueOf(params.get("days").toString());
            String reason = (String) params.get("reason");

            if (vipIds == null || vipIds.length == 0) {
                return error("请选择要延期的会员");
            }

            if (days == null || days < 1 || days > 365) {
                return error("延期天数应在1-365之间");
            }

            int successCount = 0;
            for (Long vipId : vipIds) {
                try {
                    SysUserVip vip = sysUserVipService.selectSysUserVipByVipId(vipId);
                    if (vip != null) {
                        Calendar calendar = Calendar.getInstance();
                        if (vip.getExpireTime() != null) {
                            calendar.setTime(vip.getExpireTime());
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, days);

                        vip.setExpireTime(calendar.getTime());
                        vip.setUpdateTime(new Date());
                        vip.setRemark("管理员批量延期：" + (reason != null ? reason : "无"));

                        sysUserVipService.updateSysUserVip(vip);
                        successCount++;
                    }
                } catch (Exception e) {
                    logger.error("延期会员失败: vipId={}", vipId, e);
                }
            }

            return success("批量延期完成，成功处理 " + successCount + " 个会员");
        }
        catch (Exception e)
        {
            logger.error("批量延期会员失败", e);
            return error("批量延期失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID搜索会员信息
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:list')")
    @GetMapping("/search-by-user")
    public AjaxResult searchVipByUserId(@RequestParam("userId") Long userId)
    {
        try
        {
            SysUserVip vip = sysUserVipService.selectValidVipByUserId(userId);
            if (vip != null) {
                return success(vip);
            } else {
                return success("用户暂无会员记录", null);
            }
        }
        catch (Exception e)
        {
            logger.error("搜索用户会员信息失败: userId={}", userId, e);
            return error("搜索失败");
        }
    }

    /**
     * 会员状态管理（启用/禁用）
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:edit')")
    @Log(title = "会员状态管理", businessType = BusinessType.UPDATE)
    @PostMapping("/toggle-status")
    public AjaxResult toggleVipStatus(@RequestBody Map<String, Object> params)
    {
        try
        {
            Long vipId = Long.valueOf(params.get("vipId").toString());
            String status = (String) params.get("status"); // "0"-禁用, "1"-启用
            String reason = (String) params.get("reason");

            SysUserVip vip = sysUserVipService.selectSysUserVipByVipId(vipId);
            if (vip == null) {
                return error("会员记录不存在");
            }

            vip.setVipStatus(status);
            vip.setUpdateTime(new Date());
            vip.setRemark("管理员状态变更：" + ("1".equals(status) ? "启用" : "禁用") +
                         (reason != null ? "，原因：" + reason : ""));

            sysUserVipService.updateSysUserVip(vip);

            return success("会员状态更新成功");
        }
        catch (Exception e)
        {
            logger.error("更新会员状态失败", e);
            return error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取VIP等级配置信息
     */
    @PreAuthorize("@ss.hasPermi('ai:vip:list')")
    @GetMapping("/level-config")
    public AjaxResult getVipLevelConfig()
    {
        Map<String, Object> config = new HashMap<>();

        // VIP等级配置
        Map<Integer, Map<String, Object>> levels = new HashMap<>();

        // 白银会员
        Map<String, Object> silver = new HashMap<>();
        silver.put("levelName", "白银会员");
        silver.put("dailyChatLimit", 50);
        silver.put("voiceEnabled", true);
        silver.put("imageEnabled", false);
        silver.put("customRoleQuota", 2);
        levels.put(1, silver);

        // 黄金会员
        Map<String, Object> gold = new HashMap<>();
        gold.put("levelName", "黄金会员");
        gold.put("dailyChatLimit", 150);
        gold.put("voiceEnabled", true);
        gold.put("imageEnabled", true);
        gold.put("customRoleQuota", 5);
        levels.put(2, gold);

        // 铂金会员
        Map<String, Object> platinum = new HashMap<>();
        platinum.put("levelName", "铂金会员");
        platinum.put("dailyChatLimit", 500);
        platinum.put("voiceEnabled", true);
        platinum.put("imageEnabled", true);
        platinum.put("customRoleQuota", 10);
        levels.put(3, platinum);

        // 钻石会员
        Map<String, Object> diamond = new HashMap<>();
        diamond.put("levelName", "钻石会员");
        diamond.put("dailyChatLimit", -1); // 无限
        diamond.put("voiceEnabled", true);
        diamond.put("imageEnabled", true);
        diamond.put("customRoleQuota", -1); // 无限
        levels.put(4, diamond);

        config.put("levels", levels);

        return success(config);
    }

    // 辅助方法
    private Integer getCustomRoleQuota(Integer vipLevel) {
        switch (vipLevel) {
            case 1: return 2;   // 白银
            case 2: return 5;   // 黄金
            case 3: return 10;  // 铂金
            case 4: return -1;  // 钻石（无限）
            default: return 0;
        }
    }
}
