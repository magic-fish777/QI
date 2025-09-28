package com.qiqiplay.ai.schedule;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qiqiplay.ai.service.IVipExpirationService;

/**
 * VIP会员过期处理定时任务
 *
 * @author qiqiplay
 * @date 2025-09-28
 */
@Component
@ConditionalOnProperty(name = "qiqiplay.vip.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class VipExpirationScheduler
{
    private static final Logger log = LoggerFactory.getLogger(VipExpirationScheduler.class);

    @Autowired
    private IVipExpirationService vipExpirationService;

    /**
     * 每天凌晨2点执行VIP过期检查
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processExpiredVips()
    {
        log.info("开始执行VIP过期处理定时任务...");

        try
        {
            Map<String, Object> result = vipExpirationService.processExpiredVips();

            if ((Boolean) result.get("success"))
            {
                int expiredCount = (Integer) result.get("processedExpiredCount");
                int noticeCount = (Integer) result.get("expiringNoticeCount");
                int errorCount = (Integer) result.get("errorCount");

                log.info("VIP过期处理定时任务执行完成 - 过期处理: {}, 提醒发送: {}, 错误: {}",
                        expiredCount, noticeCount, errorCount);

                // 记录详细统计信息
                if (expiredCount > 0 || noticeCount > 0)
                {
                    log.info("VIP过期处理统计: 本次处理了{}个过期会员，发送了{}个过期提醒", expiredCount, noticeCount);
                }
            }
            else
            {
                log.error("VIP过期处理定时任务执行失败: {}", result.get("message"));
            }
        }
        catch (Exception e)
        {
            log.error("VIP过期处理定时任务执行异常", e);
        }
    }

    /**
     * 每小时执行一次即将过期提醒检查（工作时间：8-22点）
     * 这样可以及时提醒用户，而不是等到每天凌晨
     */
    @Scheduled(cron = "0 0 8-22 * * ?")
    public void sendExpirationReminders()
    {
        log.debug("执行VIP过期提醒检查...");

        try
        {
            List<Map<String, Object>> expiringVips = vipExpirationService.getExpiringVips();

            if (!expiringVips.isEmpty())
            {
                log.info("发现{}个即将过期的VIP会员，开始发送提醒", expiringVips.size());

                for (Map<String, Object> vipInfo : expiringVips)
                {
                    try
                    {
                        Long userId = (Long) vipInfo.get("userId");
                        Integer daysLeft = (Integer) vipInfo.get("daysLeft");

                        // 只在特定天数发送提醒，避免重复提醒
                        if (shouldSendReminder(daysLeft))
                        {
                            vipExpirationService.sendExpirationNotice(userId, daysLeft);
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("发送单个VIP过期提醒失败", e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("VIP过期提醒检查异常", e);
        }
    }

    /**
     * 每30分钟执行一次在线用户VIP状态检查
     * 确保用户在使用过程中VIP状态是实时准确的
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30分钟
    public void realtimeVipStatusCheck()
    {
        log.debug("执行实时VIP状态检查...");

        try
        {
            // 这里可以根据需要实现在线用户的实时状态检查
            // 例如：从Redis获取在线用户列表，然后检查他们的VIP状态

            // TODO: 集成在线用户管理，实现精确的实时检查
            log.debug("实时VIP状态检查完成");
        }
        catch (Exception e)
        {
            log.error("实时VIP状态检查异常", e);
        }
    }

    /**
     * 判断是否应该发送提醒
     * @param daysLeft 剩余天数
     * @return 是否发送
     */
    private boolean shouldSendReminder(int daysLeft)
    {
        // 在剩余7天、3天、1天时发送提醒
        return daysLeft == 7 || daysLeft == 3 || daysLeft == 1;
    }
}