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
    private String name;

    @ApiModelProperty("权限编码")
    private String code;

    @ApiModelProperty("权限类型：0-目录，1-菜单，2-按钮")
    private Integer type;

    @ApiModelProperty("父级ID")
    private Long parentId;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("路由路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("权限标识")
    private String permission;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否可见：0-不可见，1-可见")
    private Integer visible;

    @ApiModelProperty("是否缓存：0-不缓存，1-缓存")
    private Integer keepAlive;

    @ApiModelProperty("是否外链：0-否，1-是")
    private Integer external;
} 