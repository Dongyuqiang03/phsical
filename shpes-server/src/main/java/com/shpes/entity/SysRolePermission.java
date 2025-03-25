package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
@ApiModel("角色权限关联")
public class SysRolePermission extends BaseRelationEntity {

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("权限ID")
    private Long permissionId;
} 