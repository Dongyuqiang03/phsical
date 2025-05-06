package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @ApiModelProperty("部门类型(1:医疗科室 2:教学院系 3:其他部门)")
    private Integer deptType;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("人员数量")
    private Integer userCount;
    
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 