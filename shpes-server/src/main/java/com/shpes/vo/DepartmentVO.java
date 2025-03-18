package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门返回值对象
 */
@Data
@ApiModel(description = "部门返回值对象")
public class DepartmentVO {
    
    @ApiModelProperty("部门ID")
    private Long id;
    
    @ApiModelProperty("父部门ID")
    private Long parentId;
    
    @ApiModelProperty("部门名称")
    private String deptName;
    
    @ApiModelProperty("部门编码")
    private String deptCode;
    
    @ApiModelProperty("部门描述")
    private String description;
    
    @ApiModelProperty("负责人")
    private String leader;
    
    @ApiModelProperty("联系电话")
    private String phone;
    
    @ApiModelProperty("排序")
    private Integer sort;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("备注")
    private String remark;
    
    @ApiModelProperty("人员数量")
    private Integer userCount;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 