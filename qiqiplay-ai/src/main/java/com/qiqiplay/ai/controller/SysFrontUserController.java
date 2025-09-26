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
import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.service.ISysFrontUserService;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import com.qiqiplay.common.core.page.TableDataInfo;

/**
 * 前台用户信息（精简版）Controller
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/ai/user")
public class SysFrontUserController extends BaseController
{
    @Autowired
    private ISysFrontUserService sysFrontUserService;

    /**
     * 查询前台用户信息（精简版）列表
     */
    @PreAuthorize("@ss.hasPermi('ai:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFrontUser sysFrontUser)
    {
        startPage();
        List<SysFrontUser> list = sysFrontUserService.selectSysFrontUserList(sysFrontUser);
        return getDataTable(list);
    }

    /**
     * 导出前台用户信息（精简版）列表
     */
    @PreAuthorize("@ss.hasPermi('ai:user:export')")
    @Log(title = "前台用户信息（精简版）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFrontUser sysFrontUser)
    {
        List<SysFrontUser> list = sysFrontUserService.selectSysFrontUserList(sysFrontUser);
        ExcelUtil<SysFrontUser> util = new ExcelUtil<SysFrontUser>(SysFrontUser.class);
        util.exportExcel(response, list, "前台用户信息（精简版）数据");
    }

    /**
     * 获取前台用户信息（精简版）详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(sysFrontUserService.selectSysFrontUserByUserId(userId));
    }

    /**
     * 新增前台用户信息（精简版）
     */
    @PreAuthorize("@ss.hasPermi('ai:user:add')")
    @Log(title = "前台用户信息（精简版）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFrontUser sysFrontUser)
    {
        return toAjax(sysFrontUserService.insertSysFrontUser(sysFrontUser));
    }

    /**
     * 修改前台用户信息（精简版）
     */
    @PreAuthorize("@ss.hasPermi('ai:user:edit')")
    @Log(title = "前台用户信息（精简版）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFrontUser sysFrontUser)
    {
        return toAjax(sysFrontUserService.updateSysFrontUser(sysFrontUser));
    }

    /**
     * 删除前台用户信息（精简版）
     */
    @PreAuthorize("@ss.hasPermi('ai:user:remove')")
    @Log(title = "前台用户信息（精简版）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(sysFrontUserService.deleteSysFrontUserByUserIds(userIds));
    }
}
