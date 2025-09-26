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
import com.qiqiplay.ai.domain.SysVoiceCache;
import com.qiqiplay.ai.service.ISysVoiceCacheService;
import com.qiqiplay.common.utils.poi.ExcelUtil;
import com.qiqiplay.common.core.page.TableDataInfo;

/**
 * 语音缓存Controller
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/ai/cache")
public class SysVoiceCacheController extends BaseController
{
    @Autowired
    private ISysVoiceCacheService sysVoiceCacheService;

    /**
     * 查询语音缓存列表
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysVoiceCache sysVoiceCache)
    {
        startPage();
        List<SysVoiceCache> list = sysVoiceCacheService.selectSysVoiceCacheList(sysVoiceCache);
        return getDataTable(list);
    }

    /**
     * 导出语音缓存列表
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:export')")
    @Log(title = "语音缓存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysVoiceCache sysVoiceCache)
    {
        List<SysVoiceCache> list = sysVoiceCacheService.selectSysVoiceCacheList(sysVoiceCache);
        ExcelUtil<SysVoiceCache> util = new ExcelUtil<SysVoiceCache>(SysVoiceCache.class);
        util.exportExcel(response, list, "语音缓存数据");
    }

    /**
     * 获取语音缓存详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:query')")
    @GetMapping(value = "/{voiceId}")
    public AjaxResult getInfo(@PathVariable("voiceId") Long voiceId)
    {
        return success(sysVoiceCacheService.selectSysVoiceCacheByVoiceId(voiceId));
    }

    /**
     * 新增语音缓存
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:add')")
    @Log(title = "语音缓存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysVoiceCache sysVoiceCache)
    {
        return toAjax(sysVoiceCacheService.insertSysVoiceCache(sysVoiceCache));
    }

    /**
     * 修改语音缓存
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:edit')")
    @Log(title = "语音缓存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysVoiceCache sysVoiceCache)
    {
        return toAjax(sysVoiceCacheService.updateSysVoiceCache(sysVoiceCache));
    }

    /**
     * 删除语音缓存
     */
    @PreAuthorize("@ss.hasPermi('ai:cache:remove')")
    @Log(title = "语音缓存", businessType = BusinessType.DELETE)
	@DeleteMapping("/{voiceIds}")
    public AjaxResult remove(@PathVariable Long[] voiceIds)
    {
        return toAjax(sysVoiceCacheService.deleteSysVoiceCacheByVoiceIds(voiceIds));
    }
}
