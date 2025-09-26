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
import com.qiqiplay.ai.domain.SysChatRecord;
import com.qiqiplay.ai.service.ISysChatRecordService;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import com.qiqiplay.common.core.page.TableDataInfo;

/**
 * 聊天记录Controller
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/ai/record")
public class SysChatRecordController extends BaseController
{
    @Autowired
    private ISysChatRecordService sysChatRecordService;

    /**
     * 查询聊天记录列表
     */
    @PreAuthorize("@ss.hasPermi('ai:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysChatRecord sysChatRecord)
    {
        startPage();
        List<SysChatRecord> list = sysChatRecordService.selectSysChatRecordList(sysChatRecord);
        return getDataTable(list);
    }

    /**
     * 导出聊天记录列表
     */
    @PreAuthorize("@ss.hasPermi('ai:record:export')")
    @Log(title = "聊天记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysChatRecord sysChatRecord)
    {
        List<SysChatRecord> list = sysChatRecordService.selectSysChatRecordList(sysChatRecord);
        ExcelUtil<SysChatRecord> util = new ExcelUtil<SysChatRecord>(SysChatRecord.class);
        util.exportExcel(response, list, "聊天记录数据");
    }

    /**
     * 获取聊天记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(sysChatRecordService.selectSysChatRecordByRecordId(recordId));
    }

    /**
     * 新增聊天记录
     */
    @PreAuthorize("@ss.hasPermi('ai:record:add')")
    @Log(title = "聊天记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysChatRecord sysChatRecord)
    {
        return toAjax(sysChatRecordService.insertSysChatRecord(sysChatRecord));
    }

    /**
     * 修改聊天记录
     */
    @PreAuthorize("@ss.hasPermi('ai:record:edit')")
    @Log(title = "聊天记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysChatRecord sysChatRecord)
    {
        return toAjax(sysChatRecordService.updateSysChatRecord(sysChatRecord));
    }

    /**
     * 删除聊天记录
     */
    @PreAuthorize("@ss.hasPermi('ai:record:remove')")
    @Log(title = "聊天记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(sysChatRecordService.deleteSysChatRecordByRecordIds(recordIds));
    }
}
