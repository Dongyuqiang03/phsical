package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 权限树形结构VO
 */
@Data
@ApiModel("权限树形结构")
public class PermissionTreeVO {

    @ApiModelProperty("权限ID")
    private Long id;

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限编码")
    private String permissionCode;

    @ApiModelProperty("权限类型(1:菜单 2:按钮)")
    private Integer permissionType;

    @ApiModelProperty("父权限ID")
    private Long parentId;

    @ApiModelProperty("子权限列表")
    private List<PermissionTreeVO> children;
}