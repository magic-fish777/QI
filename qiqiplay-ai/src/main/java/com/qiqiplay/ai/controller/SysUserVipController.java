package com.qiqiplay.ai.controller;

import java.util.List;
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
}
