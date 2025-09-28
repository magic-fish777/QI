package com.qiqiplay.chat.controller;

import com.qiqiplay.ai.domain.SysRoleAi;
import com.qiqiplay.ai.service.ISysRoleAiService;
import com.qiqiplay.common.annotation.Anonymous;
import com.qiqiplay.common.annotation.Log;
import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.core.page.TableDataInfo;
import com.qiqiplay.common.enums.BusinessType;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * AI角色信息Controller
 *
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/chat/ai")
public class FrontRoleAiController extends BaseController
{
    @Autowired
    private ISysRoleAiService sysRoleAiService;

    /**
     * 查询AI角色信息列表
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRoleAi sysRoleAi)
    {
        startPage();
        List<SysRoleAi> list = sysRoleAiService.selectSysRoleAiList(sysRoleAi);
        return getDataTable(list);
    }

    /**
     * 导出AI角色信息列表
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:export')")
    @Log(title = "AI角色信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleAi sysRoleAi)
    {
        List<SysRoleAi> list = sysRoleAiService.selectSysRoleAiList(sysRoleAi);
        ExcelUtil<SysRoleAi> util = new ExcelUtil<SysRoleAi>(SysRoleAi.class);
        util.exportExcel(response, list, "AI角色信息数据");
    }

    /**
     * 获取AI角色信息详细信息
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable("roleId") Long roleId)
    {
        return success(sysRoleAiService.selectSysRoleAiByRoleId(roleId));
    }

    /**
     * 新增AI角色信息
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:add')")
    @Log(title = "AI角色信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysRoleAi sysRoleAi)
    {
        return toAjax(sysRoleAiService.insertSysRoleAi(sysRoleAi));
    }

    /**
     * 修改AI角色信息
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:edit')")
    @Log(title = "AI角色信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysRoleAi sysRoleAi)
    {
        return toAjax(sysRoleAiService.updateSysRoleAi(sysRoleAi));
    }

    /**
     * 删除AI角色信息
     */
//    @PreAuthorize("@ss.hasPermi('ai:ai:remove')")
    @Log(title = "AI角色信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        return toAjax(sysRoleAiService.deleteSysRoleAiByRoleIds(roleIds));
    }
}
