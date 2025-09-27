package com.qiqiplay.ai.service.impl;

import java.util.List;

import com.qiqiplay.ai.domain.model.AiFrontLoginBody;
import com.qiqiplay.ai.domain.model.AiFrontLoginUser;
import com.qiqiplay.ai.domain.model.AiFrontRegisterBody;
import com.qiqiplay.ai.service.AiFrontTokenService;
import com.qiqiplay.ai.service.EmailService;
import com.qiqiplay.ai.service.VerificationCodeService;
import com.qiqiplay.common.core.domain.AjaxResult;
import com.qiqiplay.common.utils.DateUtils;
import com.qiqiplay.common.utils.StringUtils;
import com.qiqiplay.common.utils.SecurityUtils;
import com.qiqiplay.common.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqiplay.ai.mapper.SysFrontUserMapper;
import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.service.ISysFrontUserService;

import java.util.HashSet;
import java.util.Set;

/**
 * 前台用户信息（精简版）Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class SysFrontUserServiceImpl implements ISysFrontUserService
{
    @Autowired
    private SysFrontUserMapper sysFrontUserMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private AiFrontTokenService tokenService;


    /**
     *  注册前台用户信息（精简版）
     * @param registerBody
     * @return
     */
    @Override
    public AjaxResult register(AiFrontRegisterBody registerBody) {
        // 参数验证

        if (StringUtils.isEmpty(registerBody.getUsername()) || StringUtils.isEmpty(registerBody.getPassword()))
        {
            return AjaxResult.error("用户名和密码不能为空");
        }

        if (StringUtils.isEmpty(registerBody.getEmail()))
        {
            return  AjaxResult.error("邮箱不能为空");
        }

        if (StringUtils.isEmpty(registerBody.getCode()))
        {
            return AjaxResult.error("邮箱验证码不能为空");
        }

        if (!registerBody.getPassword().equals(registerBody.getConfirmPassword()))
        {
            return AjaxResult.error("两次输入的密码不一致");
        }

        if (registerBody.getAgreement() == null || !registerBody.getAgreement())
        {
            return AjaxResult.error("请同意用户协议");
        }

        // 验证邮箱验证码
        if (!verificationCodeService.verifyCode(registerBody.getEmail(), registerBody.getCode())) {
            return AjaxResult.error("验证码错误或已过期");
        }
        //todo 这里可用布隆过滤或者redis缓存来优化
        if (sysFrontUserMapper.selectSysFrontUserByUsername(registerBody.getUsername()) != null)
        {
            return AjaxResult.error("用户名已存在");
        }

        //开始注册
        SysFrontUser sysFrontUser = new SysFrontUser();
        sysFrontUser.setUserName(registerBody.getUsername());
        sysFrontUser.setNickName(registerBody.getNickName());
        sysFrontUser.setEmail(registerBody.getEmail());
        sysFrontUser.setPhonenumber(registerBody.getPhone());
        sysFrontUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
        sysFrontUser.setStatus("0"); // 正常状态
        sysFrontUser.setDelFlag("0"); // 未删除

        int result = insertSysFrontUser(sysFrontUser);
        if (result > 0) {
            // 注册成功后清除验证码
            verificationCodeService.removeCode(registerBody.getEmail());
            return AjaxResult.success("注册成功");
        } else {
            return AjaxResult.error("注册失败");
        }
    }

    /**
     * 前台用户登录
     * @param loginBody
     * @return
     */
    @Override
    public AjaxResult login(AiFrontLoginBody loginBody) {
        // 参数验证
        if (StringUtils.isEmpty(loginBody.getUsername()) || StringUtils.isEmpty(loginBody.getPassword())) {
            return AjaxResult.error("用户名和密码不能为空");
        }

        // 根据用户名或邮箱查询用户
        SysFrontUser user = sysFrontUserMapper.selectSysFrontUserByUsername(loginBody.getUsername());
        if (user == null) {
            user = sysFrontUserMapper.selectSysFrontUserByEmail(loginBody.getUsername());
        }

        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        // 检查用户状态
        if (!"0".equals(user.getStatus())) {
            return AjaxResult.error("用户已被禁用");
        }

        // 验证密码
        if (!SecurityUtils.matchesPassword(loginBody.getPassword(), user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        // 生成token或返回用户信息
        return AjaxResult.success("登录成功", user);
    }

    /**
     * 查询前台用户信息（精简版）
     *
     * @param userId 前台用户信息（精简版）主键
     * @return 前台用户信息（精简版）
     */
    @Override
    public SysFrontUser selectSysFrontUserByUserId(Long userId)
    {
        return sysFrontUserMapper.selectSysFrontUserByUserId(userId);
    }

    /**
     * 查询前台用户信息（精简版）列表
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 前台用户信息（精简版）
     */
    @Override
    public List<SysFrontUser> selectSysFrontUserList(SysFrontUser sysFrontUser)
    {
        return sysFrontUserMapper.selectSysFrontUserList(sysFrontUser);
    }

    /**
     * 新增前台用户信息（精简版）
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    @Override
    public int insertSysFrontUser(SysFrontUser sysFrontUser)
    {
        sysFrontUser.setCreateTime(DateUtils.getNowDate());
        return sysFrontUserMapper.insertSysFrontUser(sysFrontUser);
    }

    /**
     * 修改前台用户信息（精简版）
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    @Override
    public int updateSysFrontUser(SysFrontUser sysFrontUser)
    {
        sysFrontUser.setUpdateTime(DateUtils.getNowDate());
        return sysFrontUserMapper.updateSysFrontUser(sysFrontUser);
    }

    /**
     * 批量删除前台用户信息（精简版）
     *
     * @param userIds 需要删除的前台用户信息（精简版）主键
     * @return 结果
     */
    @Override
    public int deleteSysFrontUserByUserIds(Long[] userIds)
    {
        return sysFrontUserMapper.deleteSysFrontUserByUserIds(userIds);
    }

    /**
     * 删除前台用户信息（精简版）信息
     *
     * @param userId 前台用户信息（精简版）主键
     * @return 结果
     */
    @Override
    public int deleteSysFrontUserByUserId(Long userId)
    {
        return sysFrontUserMapper.deleteSysFrontUserByUserId(userId);
    }

    @Override
    public String login(String username, String password) {
        // 根据用户名或邮箱查询用户
        SysFrontUser user = sysFrontUserMapper.selectSysFrontUserByUsername(username);
        if (user == null) {
            user = sysFrontUserMapper.selectSysFrontUserByEmail(username);
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (!"0".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT token
        Set<String> permissions = new HashSet<>();
        permissions.add("ai:chat:access"); // AI聊天访问权限
        AiFrontLoginUser loginUser = new AiFrontLoginUser(user.getUserId(), user, permissions);
        return tokenService.createToken(loginUser);
    }

    @Override
    public String loginByEmail(String email, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 查询用户
        SysFrontUser user = sysFrontUserMapper.selectSysFrontUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (!"0".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证码验证成功后清除
        verificationCodeService.removeCode(email);

        // 生成JWT token
        Set<String> permissions = new HashSet<>();
        permissions.add("ai:chat:access"); // AI聊天访问权限
        AiFrontLoginUser loginUser = new AiFrontLoginUser(user.getUserId(), user, permissions);
        return tokenService.createToken(loginUser);
    }

    @Override
    public String sendEmailCode(String email) {
        try {
            // 生成验证码
            String code = emailService.generateVerificationCode();

            // 发送邮件
            if (emailService.sendVerificationCode(email, code)) {
                // 存储验证码到Redis
                verificationCodeService.storeCode(email, code);
                return null; // 成功返回null
            } else {
                return "邮件发送失败";
            }
        } catch (Exception e) {
            return "邮件发送异常：" + e.getMessage();
        }
    }

    /**
     * 根据token获取用户信息
     * @return 用户信息
     */
    @Override
    public AjaxResult getUserInfo() {
        try {
            // 从当前请求中获取登录用户信息
            AiFrontLoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            System.out.println("==> [AI认证] 从token服务获取用户: " + (loginUser != null ? "成功" : "失败"));

            if (loginUser == null) {
                System.out.println("==> [AI认证] 用户未登录，返回错误");
                return AjaxResult.error("用户未登录");
            }

            System.out.println("==> [AI认证] 用户ID: " + loginUser.getUserId() + ", Token: " + (loginUser.getToken() != null ? "存在" : "不存在"));

            // 验证token是否过期
            tokenService.verifyToken(loginUser);
            System.out.println("==> [AI认证] Token验证完成");

            // 获取用户详细信息
            SysFrontUser user = loginUser.getFrontUser();
            if (user == null) {
                System.out.println("==> [AI认证] 前台用户信息为空");
                return AjaxResult.error("用户信息不存在");
            }

            System.out.println("==> [AI认证] 获取用户信息成功, 用户名: " + user.getUserName());

            AjaxResult ajax = AjaxResult.success();
            ajax.put("user", user);
            ajax.put("permissions", loginUser.getPermissions());
            return ajax;

        } catch (Exception e) {
            System.out.println("==> [AI认证] 获取用户信息异常: " + e.getMessage());
            e.printStackTrace();
            return AjaxResult.error("获取用户信息失败: " + e.getMessage());
        }
    }

    public void logout(String token) {
        // 清除token相关的缓存
        tokenService.delLoginUser(token);
    }

}
