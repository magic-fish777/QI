package com.qiqiplay.ai.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * AI角色信息对象 sys_role_ai
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysRoleAi extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private Long roleId;

    /** 角色名称（如哈利波特） */
    @Excel(name = "角色名称", readConverterExp = "如=哈利波特")
    private String roleName;

    /** 角色key */
    @Excel(name = "角色key")
    private String roleKey;

    /** 角色身份描述 */
    @Excel(name = "角色身份描述")
    private String roleIdentity;

    /** 角色分类（影视/教育等） */
    @Excel(name = "角色分类", readConverterExp = "影=视/教育等")
    private String roleCategory;

    /** 角色头像URL */
    @Excel(name = "角色头像URL")
    private String roleAvatar;

    /** 是否定制角色（0系统 1用户定制） */
    @Excel(name = "是否定制角色", readConverterExp = "0=系统,1=用户定制")
    private String isCustom;

    /** 创建者ID（关联sys_user.user_id） */
    @Excel(name = "创建者ID", readConverterExp = "关=联sys_user.user_id")
    private Long creatorUserId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setRoleId(Long roleId) 
    {
        this.roleId = roleId;
    }

    public Long getRoleId() 
    {
        return roleId;
    }

    public void setRoleName(String roleName) 
    {
        this.roleName = roleName;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleKey(String roleKey)
    {
        this.roleKey = roleKey;
    }

    public String getRoleKey()
    {
        return roleKey;
    }

    public void setRoleIdentity(String roleIdentity) 
    {
        this.roleIdentity = roleIdentity;
    }

    public String getRoleIdentity() 
    {
        return roleIdentity;
    }

    public void setRoleCategory(String roleCategory) 
    {
        this.roleCategory = roleCategory;
    }

    public String getRoleCategory() 
    {
        return roleCategory;
    }

    public void setRoleAvatar(String roleAvatar) 
    {
        this.roleAvatar = roleAvatar;
    }

    public String getRoleAvatar() 
    {
        return roleAvatar;
    }

    public void setIsCustom(String isCustom) 
    {
        this.isCustom = isCustom;
    }

    public String getIsCustom() 
    {
        return isCustom;
    }

    public void setCreatorUserId(Long creatorUserId) 
    {
        this.creatorUserId = creatorUserId;
    }

    public Long getCreatorUserId() 
    {
        return creatorUserId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId())
            .append("roleName", getRoleName())
            .append("roleIdentity", getRoleIdentity())
            .append("roleCategory", getRoleCategory())
            .append("roleAvatar", getRoleAvatar())
            .append("isCustom", getIsCustom())
            .append("creatorUserId", getCreatorUserId())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
