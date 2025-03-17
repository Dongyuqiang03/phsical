package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
@ApiModel("系统权限")
public class SysPermission extends BaseEntity {

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限编码")
    private String permissionCode;

    @ApiModelProperty("权限类型(1:菜单 2:按钮)")
    private Integer permissionType;

    @ApiModelProperty("父权限ID")
    private Long parentId;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态(0:禁用 1:启用)")
    private Integer status;
}