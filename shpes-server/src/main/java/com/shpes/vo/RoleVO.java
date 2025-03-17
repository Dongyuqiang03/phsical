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
}