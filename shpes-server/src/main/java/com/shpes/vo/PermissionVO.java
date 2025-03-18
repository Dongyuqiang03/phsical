package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("权限VO")
public class PermissionVO {
    @ApiModelProperty("权限ID")
    private Long id;

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限编码")
    private String permissionCode;

    @ApiModelProperty("权限类型")
    private Integer permissionType;
}