package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色返回值对象
 */
@Data
@ApiModel(description = "角色返回值对象")
public class RoleVO {
    
    @ApiModelProperty("角色ID")
    private Long id;
    
    @ApiModelProperty("角色名称")
    private String name;
    
    @ApiModelProperty("角色编码")
    private String code;
    
    @ApiModelProperty("角色描述")
    private String description;
    
    @ApiModelProperty("排序")
    private Integer sort;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    
    @ApiModelProperty("权限ID列表")
    private List<Long> permissionIds;
} 