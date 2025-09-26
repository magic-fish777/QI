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
import com.qiqiplay.ai.domain.SysSensitiveWord;
import com.qiqiplay.ai.service.ISysSensitiveWordService;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import com.qiqiplay.common.core.page.TableDataInfo;

/**
 * 敏感词库Controller
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/ai/word")
public class SysSensitiveWordController extends BaseController
{
    @Autowired
    private ISysSensitiveWordService sysSensitiveWordService;

    /**
     * 查询敏感词库列表
     */
    @PreAuthorize("@ss.hasPermi('ai:word:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysSensitiveWord sysSensitiveWord)
    {
        startPage();
        List<SysSensitiveWord> list = sysSensitiveWordService.selectSysSensitiveWordList(sysSensitiveWord);
        return getDataTable(list);
    }

    /**
     * 导出敏感词库列表
     */
    @PreAuthorize("@ss.hasPermi('ai:word:export')")
    @Log(title = "敏感词库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysSensitiveWord sysSensitiveWord)
    {
        List<SysSensitiveWord> list = sysSensitiveWordService.selectSysSensitiveWordList(sysSensitiveWord);
        ExcelUtil<SysSensitiveWord> util = new ExcelUtil<SysSensitiveWord>(SysSensitiveWord.class);
        util.exportExcel(response, list, "敏感词库数据");
    }

    /**
     * 获取敏感词库详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:word:query')")
    @GetMapping(value = "/{wordId}")
    public AjaxResult getInfo(@PathVariable("wordId") Long wordId)
    {
        return success(sysSensitiveWordService.selectSysSensitiveWordByWordId(wordId));
    }

    /**
     * 新增敏感词库
     */
    @PreAuthorize("@ss.hasPermi('ai:word:add')")
    @Log(title = "敏感词库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysSensitiveWord sysSensitiveWord)
    {
        return toAjax(sysSensitiveWordService.insertSysSensitiveWord(sysSensitiveWord));
    }

    /**
     * 修改敏感词库
     */
    @PreAuthorize("@ss.hasPermi('ai:word:edit')")
    @Log(title = "敏感词库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysSensitiveWord sysSensitiveWord)
    {
        return toAjax(sysSensitiveWordService.updateSysSensitiveWord(sysSensitiveWord));
    }

    /**
     * 删除敏感词库
     */
    @PreAuthorize("@ss.hasPermi('ai:word:remove')")
    @Log(title = "敏感词库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{wordIds}")
    public AjaxResult remove(@PathVariable Long[] wordIds)
    {
        return toAjax(sysSensitiveWordService.deleteSysSensitiveWordByWordIds(wordIds));
    }
}
