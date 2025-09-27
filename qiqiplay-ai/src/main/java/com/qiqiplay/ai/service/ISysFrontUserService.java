package com.qiqiplay.ai.service;

import java.util.List;
import com.qiqiplay.ai.domain.SysFrontUser;
import com.qiqiplay.ai.domain.model.AiFrontLoginBody;
import com.qiqiplay.ai.domain.model.AiFrontRegisterBody;
import com.qiqiplay.common.core.domain.AjaxResult;

/**
 * 前台用户信息（精简版）Service接口
 *
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ISysFrontUserService
{
    /**
     * 查询前台用户信息（精简版）
     *
     * @param userId 前台用户信息（精简版）主键
     * @return 前台用户信息（精简版）
     */
    public SysFrontUser selectSysFrontUserByUserId(Long userId);

    /**
     * 查询前台用户信息（精简版）列表
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 前台用户信息（精简版）集合
     */
    public List<SysFrontUser> selectSysFrontUserList(SysFrontUser sysFrontUser);

    /**
     * 新增前台用户信息（精简版）
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    public int insertSysFrontUser(SysFrontUser sysFrontUser);

    /**
     * 修改前台用户信息（精简版）
     *
     * @param sysFrontUser 前台用户信息（精简版）
     * @return 结果
     */
    public int updateSysFrontUser(SysFrontUser sysFrontUser);

    /**
     * 批量删除前台用户信息（精简版）
     *
     * @param userIds 需要删除的前台用户信息（精简版）主键集合
     * @return 结果
     */
    public int deleteSysFrontUserByUserIds(Long[] userIds);

    /**
     * 删除前台用户信息（精简版）信息
     *
     * @param userId 前台用户信息（精简版）主键
     * @return 结果
     */
    public int deleteSysFrontUserByUserId(Long userId);


    /**
     * 前台用户注册
     * @param registerBody 注册信息
     * @return 注册结果
     */
    public AjaxResult register(AiFrontRegisterBody registerBody);

    /**
     * 前台用户登录
     * @param loginBody 登录信息
     * @return 登录结果
     */
    public AjaxResult login(AiFrontLoginBody loginBody);

    /**
     * 账号密码登录
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public String login(String username, String password);

    /**
     * 邮箱验证码登录
     * @param email 邮箱
     * @param code 验证码
     * @return token
     */
    public String loginByEmail(String email, String code);

    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     * @return 错误信息，为空表示成功
     */
    public String sendEmailCode(String email);

    /**
     * 退出登录
     * @param token 用户token
     */
    public void logout(String token);

    /**
     * 根据token获取用户信息
     * @return 用户信息
     */
    public AjaxResult getUserInfo();
}
