package com.shpes.vo;

import com.shpes.entity.SysPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 权限树形结构VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("权限树形结构")
public class PermissionTreeVO extends SysPermission {

    @ApiModelProperty("子权限列表")
    private List<PermissionTreeVO> children;
}