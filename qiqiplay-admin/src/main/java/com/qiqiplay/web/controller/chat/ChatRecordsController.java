package com.qiqiplay.web.controller.chat;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.core.page.TableDataInfo;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.web.controller.chat.utils.ChatSecurityUtils;
import com.qiqiplay.ai.domain.SysChatRecord;
import com.qiqiplay.ai.service.ISysChatRecordService;

/**
 * 前台聊天记录接口控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/chat/records")
public class ChatRecordsController extends BaseController
{
    @Autowired
    private ISysChatRecordService chatRecordService;
    private final Logger logger = LoggerFactory.getLogger(ChatRecordsController.class);
    /**
     * 获取当前用户的聊天记录列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param roleId 角色ID（可选）
     * @return 聊天记录列表
     */
    @GetMapping("/list")
    public TableDataInfo getUserChatRecords(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "20") int pageSize,
                                          @RequestParam(required = false) Long roleId)
    {
        try
        {
            // 获取当前用户ID - 使用兼容前台用户的工具类
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            logger.info("获取当前用户ID：" + currentUserId);
            // 设置分页参数
            startPage();

            // 构建查询条件
            SysChatRecord queryRecord = new SysChatRecord();
            queryRecord.setUserId(currentUserId);
            if (roleId != null)
            {
                queryRecord.setRoleId(roleId);
            }

            // 查询聊天记录
            List<SysChatRecord> list = chatRecordService.selectSysChatRecordList(queryRecord);
            return getDataTable(list);
        }
        catch (Exception e)
        {
            logger.error("获取聊天记录失败", e);
            return getDataTable(List.of());
        }
    }

    /**
     * 获取与特定角色的聊天记录
     *
     * @param roleId 角色ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 聊天记录列表
     */
    @GetMapping("/role/{roleId}")
    public TableDataInfo getRoleChatRecords(@PathVariable Long roleId,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "50") int pageSize)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return getDataTable(List.of());
            }

            // 设置分页参数
            startPage();

            // 构建查询条件
            SysChatRecord queryRecord = new SysChatRecord();
            queryRecord.setUserId(currentUserId);
            queryRecord.setRoleId(roleId);

            // 查询聊天记录
            List<SysChatRecord> list = chatRecordService.selectSysChatRecordList(queryRecord);
            return getDataTable(list);
        }
        catch (Exception e)
        {
            logger.error("获取角色聊天记录失败", e);
            return getDataTable(List.of());
        }
    }

    /**
     * 获取聊天记录详情
     *
     * @param recordId 记录ID
     * @return 聊天记录详情
     */
    @GetMapping("/{recordId}")
    public AjaxResult getChatRecordDetail(@PathVariable Long recordId)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 查询记录详情
            SysChatRecord record = chatRecordService.selectSysChatRecordByRecordId(recordId);

            // 验证记录是否属于当前用户
            if (record == null || !currentUserId.equals(record.getUserId()))
            {
                return error("记录不存在或无权限访问");
            }

            return success(record);
        }
        catch (Exception e)
        {
            logger.error("获取聊天记录详情失败", e);
            return error("获取记录详情失败");
        }
    }

    /**
     * 删除聊天记录
     *
     * @param recordId 记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{recordId}")
    public AjaxResult deleteChatRecord(@PathVariable Long recordId)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 先查询记录验证权限
            SysChatRecord record = chatRecordService.selectSysChatRecordByRecordId(recordId);
            if (record == null || !currentUserId.equals(record.getUserId()))
            {
                return error("记录不存在或无权限删除");
            }

            // 删除记录
            int result = chatRecordService.deleteSysChatRecordByRecordId(recordId);
            return result > 0 ? success() : error("删除失败");
        }
        catch (Exception e)
        {
            logger.error("删除聊天记录失败", e);
            return error("删除记录失败");
        }
    }

    /**
     * 获取聊天统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getChatStatistics()
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 构建查询条件
            SysChatRecord queryRecord = new SysChatRecord();
            queryRecord.setUserId(currentUserId);

            // 获取所有记录（不分页）
            List<SysChatRecord> allRecords = chatRecordService.selectSysChatRecordList(queryRecord);

            // 统计信息
            long totalRecords = allRecords.size();

            // 统计不同角色数量
            long totalRoles = allRecords.stream()
                .map(SysChatRecord::getRoleId)
                .distinct()
                .count();

            // 统计今日对话数量
            LocalDate today = LocalDate.now();
            long todayRecords = allRecords.stream()
                .filter(record -> {
                    Date chatTime = record.getChatTime();
                    if (chatTime == null) {
                        return false;
                    }
                    LocalDate recordDate = chatTime.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                    return recordDate.equals(today);
                })
                .count();

            // 构建响应数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalRecords", totalRecords);
            statistics.put("totalRoles", totalRoles);
            statistics.put("todayRecords", todayRecords);

            return success(statistics);
        }
        catch (Exception e)
        {
            logger.error("获取聊天统计失败", e);
            return error("获取统计信息失败");
        }
    }

    /**
     * 搜索聊天记录
     *
     * @param keyword 搜索关键词
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public TableDataInfo searchChatRecords(@RequestParam String keyword,
                                         @RequestParam(defaultValue = "10") int pageSize)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return getDataTable(List.of());
            }

            // 设置分页参数
            startPage();

            // 构建查询条件
            SysChatRecord queryRecord = new SysChatRecord();
            queryRecord.setUserId(currentUserId);
            queryRecord.setUserInputContent(keyword); // 用于模糊查询用户输入内容

            // 查询聊天记录
            List<SysChatRecord> list = chatRecordService.selectSysChatRecordList(queryRecord);
            return getDataTable(list);
        }
        catch (Exception e)
        {
            logger.error("搜索聊天记录失败", e);
            return getDataTable(List.of());
        }
    }
}
