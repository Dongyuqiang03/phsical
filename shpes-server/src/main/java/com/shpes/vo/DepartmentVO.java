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
    
    @ApiModelProperty("部门名称")
    private String name;
    
    @ApiModelProperty("部门编码")
    private String code;
    
    @ApiModelProperty("部门主任ID")
    private Long leaderId;
    
    @ApiModelProperty("部门主任姓名")
    private String leaderName;
    
    @ApiModelProperty("联系电话")
    private String phone;
    
    @ApiModelProperty("部门位置")
    private String location;
    
    @ApiModelProperty("部门说明")
    private String description;
    
    @ApiModelProperty("排序")
    private Integer sort;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("人员数量")
    private Integer userCount;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 