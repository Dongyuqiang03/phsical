package com.shpes.vo;

import com.shpes.entity.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 角色返回值对象
 */
@Data
@ApiModel("角色详情VO")
public class RoleVO extends SysRole {
    @ApiModelProperty("权限列表")
    private List<PermissionVO> permissions;

    /**
     * 将角色实体转换为VO对象
     */
    public static RoleVO convertToVO(SysRole role) {
        if (role == null) {
            return null;
        }
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());
        return vo;
    }
}