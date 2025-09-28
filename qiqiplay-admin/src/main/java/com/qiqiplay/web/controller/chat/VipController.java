package com.qiqiplay.web.controller.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qiqiplay.common.core.controller.BaseController;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.web.controller.chat.utils.ChatSecurityUtils;
import com.qiqiplay.ai.domain.SysUserVip;
import com.qiqiplay.ai.domain.SysVipLevel;
import com.qiqiplay.ai.domain.SysVipOrder;
import com.qiqiplay.ai.service.ISysUserVipService;
import com.qiqiplay.ai.service.IVipPaymentService;
import com.qiqiplay.ai.service.IVipStatusService;

/**
 * 前台会员功能控制器
 *
 * @author qiqiplay
 */
@RestController
@RequestMapping("/chat/vip")
public class VipController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(VipController.class);

    @Autowired
    private ISysUserVipService sysUserVipService;

    @Autowired
    private IVipPaymentService vipPaymentService;

    @Autowired
    private IVipStatusService vipStatusService;

    /**
     * 获取当前用户的会员信息
     *
     * @return 会员信息
     */
    @GetMapping("/info")
    public AjaxResult getUserVipInfo()
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 查询用户会员信息
            SysUserVip userVip = sysUserVipService.selectValidVipByUserId(currentUserId);

            Map<String, Object> result = new HashMap<>();

            if (userVip != null && "1".equals(userVip.getVipStatus()))
            {
                // 用户有有效的会员
                result.put("isVip", true);
                result.put("vipLevel", userVip.getVipLevel());
                result.put("vipType", userVip.getVipType());
                result.put("expireTime", userVip.getExpireTime());
                result.put("customRoleQuota", userVip.getCustomRoleQuota());

                // 计算剩余天数
                Date expireTime = userVip.getExpireTime();
                if (expireTime != null)
                {
                    long diffTime = expireTime.getTime() - System.currentTimeMillis();
                    int remainingDays = (int) (diffTime / (24 * 60 * 60 * 1000));
                    result.put("remainingDays", Math.max(0, remainingDays));
                }

                // 根据等级获取权益信息
                result.put("privileges", getPrivilegesByLevel(userVip.getVipLevel()));
            }
            else
            {
                // 普通用户
                result.put("isVip", false);
                result.put("vipLevel", 0);
                result.put("vipType", null);
                result.put("expireTime", null);
                result.put("customRoleQuota", 0);
                result.put("remainingDays", 0);
                result.put("privileges", getPrivilegesByLevel(0));
            }

            return success(result);
        }
        catch (Exception e)
        {
            log.error("获取用户会员信息失败", e);
            return error("获取会员信息失败");
        }
    }

    /**
     * 获取会员套餐列表
     *
     * @return 会员套餐信息
     */
    @GetMapping("/packages")
    public AjaxResult getVipPackages()
    {
        try
        {
            // 这里返回会员套餐信息，实际应该从数据库或配置中读取
            Map<String, Object> packages = new HashMap<>();

            // 白银会员套餐
            Map<String, Object> silver = new HashMap<>();
            silver.put("level", 1);
            silver.put("levelName", "白银会员");
            silver.put("monthlyPrice", 1999); // 19.99元
            silver.put("quarterlyPrice", 4999); // 49.99元
            silver.put("yearlyPrice", 15999); // 159.99元
            silver.put("privileges", getPrivilegesByLevel(1));
            packages.put("silver", silver);

            // 黄金会员套餐
            Map<String, Object> gold = new HashMap<>();
            gold.put("level", 2);
            gold.put("levelName", "黄金会员");
            gold.put("monthlyPrice", 2999); // 29.99元
            gold.put("quarterlyPrice", 7999); // 79.99元
            gold.put("yearlyPrice", 25999); // 259.99元
            gold.put("privileges", getPrivilegesByLevel(2));
            packages.put("gold", gold);

            // 铂金会员套餐
            Map<String, Object> platinum = new HashMap<>();
            platinum.put("level", 3);
            platinum.put("levelName", "铂金会员");
            platinum.put("monthlyPrice", 4999); // 49.99元
            platinum.put("quarterlyPrice", 12999); // 129.99元
            platinum.put("yearlyPrice", 39999); // 399.99元
            platinum.put("privileges", getPrivilegesByLevel(3));
            packages.put("platinum", platinum);

            // 钻石会员套餐
            Map<String, Object> diamond = new HashMap<>();
            diamond.put("level", 4);
            diamond.put("levelName", "钻石会员");
            diamond.put("monthlyPrice", 9999); // 99.99元
            diamond.put("quarterlyPrice", 24999); // 249.99元
            diamond.put("yearlyPrice", 79999); // 799.99元
            diamond.put("privileges", getPrivilegesByLevel(4));
            packages.put("diamond", diamond);

            return success(packages);
        }
        catch (Exception e)
        {
            log.error("获取会员套餐失败", e);
            return error("获取会员套餐失败");
        }
    }

    /**
     * 根据会员等级获取权益信息
     *
     * @param level 会员等级
     * @return 权益信息
     */
    private Map<String, Object> getPrivilegesByLevel(Integer level)
    {
        Map<String, Object> privileges = new HashMap<>();

        switch (level == null ? 0 : level)
        {
            case 0: // 普通用户
                privileges.put("dailyChatLimit", 10);
                privileges.put("dailyImageLimit", 0);
                privileges.put("customRoleQuota", 0);
                privileges.put("voiceEnabled", false);
                privileges.put("imageEnabled", false);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", false);
                privileges.put("prioritySupportEnabled", false);
                break;
            case 1: // 白银会员
                privileges.put("dailyChatLimit", 50);
                privileges.put("dailyImageLimit", 0);
                privileges.put("customRoleQuota", 2);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", false);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", false);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 2: // 黄金会员
                privileges.put("dailyChatLimit", 150);
                privileges.put("dailyImageLimit", 10);
                privileges.put("customRoleQuota", 5);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", false);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 3: // 铂金会员
                privileges.put("dailyChatLimit", 500);
                privileges.put("dailyImageLimit", 30);
                privileges.put("customRoleQuota", 10);
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", true);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
            case 4: // 钻石会员
                privileges.put("dailyChatLimit", -1); // 无限
                privileges.put("dailyImageLimit", -1); // 无限
                privileges.put("customRoleQuota", -1); // 无限
                privileges.put("voiceEnabled", true);
                privileges.put("imageEnabled", true);
                privileges.put("exclusiveRoleEnabled", true);
                privileges.put("chatExportEnabled", true);
                privileges.put("prioritySupportEnabled", true);
                break;
        }

        return privileges;
    }

    /**
     * 创建VIP购买订单
     *
     * @param requestData 购买请求数据
     * @return 订单和支付信息
     */
    @PostMapping("/purchase")
    public AjaxResult createVipPurchaseOrder(@RequestBody Map<String, Object> requestData)
    {
        try
        {
            // 获取当前用户ID
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            // 解析请求参数
            Integer vipLevel = (Integer) requestData.get("vipLevel");
            String periodType = (String) requestData.get("periodType"); // monthly, quarterly, yearly
            String paymentMethod = (String) requestData.get("paymentMethod"); // alipay, wechat

            if (vipLevel == null || periodType == null || paymentMethod == null)
            {
                return error("参数不完整");
            }

            // 验证VIP等级
            if (vipLevel < 1 || vipLevel > 4)
            {
                return error("无效的VIP等级");
            }

            // 创建订单
            SysVipOrder order = vipPaymentService.createVipOrder(currentUserId, vipLevel, periodType, paymentMethod);
            if (order == null)
            {
                return error("创建订单失败");
            }

            // 获取支付参数
            Map<String, Object> paymentParams = vipPaymentService.getPaymentParams(order);

            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("paymentParams", paymentParams);

            return success(result);
        }
        catch (Exception e)
        {
            log.error("创建VIP购买订单失败", e);
            return error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 模拟支付成功（开发测试用）
     *
     * @param requestData 支付数据
     * @return 处理结果
     */
    @PostMapping("/mock-payment-success")
    public AjaxResult mockPaymentSuccess(@RequestBody Map<String, Object> requestData)
    {
        try
        {
            String orderNo = (String) requestData.get("orderNo");
            String paymentMethod = (String) requestData.get("paymentMethod");

            if (orderNo == null || paymentMethod == null)
            {
                return error("参数不完整");
            }

            // 模拟第三方交易号
            String transactionId = "MOCK_" + System.currentTimeMillis();

            // 处理支付成功
            boolean success = vipPaymentService.handlePaymentSuccess(orderNo, transactionId, paymentMethod);

            if (success)
            {
                return success("支付成功，VIP已开通");
            }
            else
            {
                return error("支付处理失败");
            }
        }
        catch (Exception e)
        {
            log.error("模拟支付成功失败", e);
            return error("支付处理失败：" + e.getMessage());
        }
    }

    /**
     * 支付成功回调接口（实际支付平台回调）
     *
     * @param requestData 回调数据
     * @return 处理结果
     */
    @PostMapping("/payment-callback")
    public AjaxResult paymentCallback(@RequestBody Map<String, Object> requestData)
    {
        try
        {
            log.info("收到支付回调: {}", requestData);

            String orderNo = (String) requestData.get("orderNo");
            String transactionId = (String) requestData.get("transactionId");
            String paymentMethod = (String) requestData.get("paymentMethod");

            if (orderNo == null || transactionId == null)
            {
                return error("回调参数不完整");
            }

            // 处理支付成功
            boolean success = vipPaymentService.handlePaymentSuccess(orderNo, transactionId, paymentMethod);

            if (success)
            {
                return success("回调处理成功");
            }
            else
            {
                return error("回调处理失败");
            }
        }
        catch (Exception e)
        {
            log.error("支付回调处理失败", e);
            return error("回调处理失败");
        }
    }

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @GetMapping("/order-status")
    public AjaxResult getOrderStatus(String orderNo)
    {
        try
        {
            if (orderNo == null || orderNo.trim().isEmpty())
            {
                return error("订单号不能为空");
            }

            SysVipOrder order = vipPaymentService.getOrderByOrderNo(orderNo);
            if (order == null)
            {
                return error("订单不存在");
            }

            return success(order);
        }
        catch (Exception e)
        {
            log.error("查询订单状态失败", e);
            return error("查询失败");
        }
    }

    /**
     * 获取实时VIP状态
     *
     * @return 实时VIP状态信息
     */
    @GetMapping("/realtime-status")
    public AjaxResult getRealtimeVipStatus()
    {
        try
        {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            Map<String, Object> status = vipStatusService.checkAndRefreshVipStatus(currentUserId);
            return success(status);
        }
        catch (Exception e)
        {
            log.error("获取实时VIP状态失败", e);
            return error("获取状态失败");
        }
    }

    /**
     * 验证功能权限
     *
     * @param feature 功能名称（chat, voice, image）
     * @return 权限验证结果
     */
    @GetMapping("/validate-permission")
    public AjaxResult validateFeaturePermission(String feature)
    {
        try
        {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            if (feature == null || feature.trim().isEmpty())
            {
                return error("功能名称不能为空");
            }

            Map<String, Object> permission = vipStatusService.validateFeaturePermission(currentUserId, feature);
            return success(permission);
        }
        catch (Exception e)
        {
            log.error("验证功能权限失败", e);
            return error("权限验证失败");
        }
    }

    /**
     * 获取今日剩余使用次数
     *
     * @return 各功能剩余使用次数
     */
    @GetMapping("/remaining-usage")
    public AjaxResult getTodayRemainingUsage()
    {
        try
        {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            Map<String, Object> remaining = vipStatusService.getTodayRemainingUsage(currentUserId);
            return success(remaining);
        }
        catch (Exception e)
        {
            log.error("获取今日剩余使用次数失败", e);
            return error("获取失败");
        }
    }

    /**
     * 检查是否可以使用某个功能
     *
     * @param feature 功能名称
     * @return 是否可以使用
     */
    @GetMapping("/can-use-feature")
    public AjaxResult canUseFeature(String feature)
    {
        try
        {
            Long currentUserId = ChatSecurityUtils.getCurrentUserId();
            if (currentUserId == null)
            {
                return error("用户未登录");
            }

            if (feature == null || feature.trim().isEmpty())
            {
                return error("功能名称不能为空");
            }

            boolean canUse = vipStatusService.canUseFeature(currentUserId, feature);
            Map<String, Object> result = new HashMap<>();
            result.put("canUse", canUse);
            result.put("feature", feature);

            return success(result);
        }
        catch (Exception e)
        {
            log.error("检查功能使用权限失败", e);
            return error("检查失败");
        }
    }
}