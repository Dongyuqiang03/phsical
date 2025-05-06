package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 部门DTO
 */
@Data
@ApiModel("部门请求参数")
public class DepartmentDTO {

    @ApiModelProperty("父部门ID")
    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    @ApiModelProperty("部门名称")
    private String deptName;

    @NotBlank(message = "部门编码不能为空")
    @ApiModelProperty("部门编码")
    private String deptCode;

    @NotNull(message = "部门类型不能为空")
    @ApiModelProperty("部门类型(1:医疗科室 2:教学院系 3:其他部门)")
    private Integer deptType;

    @ApiModelProperty("部门描述")
    private String description;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status = 1;  // 默认启用
}