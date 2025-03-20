package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户查询参数
 */
@Data
@ApiModel("用户查询参数")
public class UserQueryDTO {
    
    @ApiModelProperty("页码")
    private Integer pageNum = 1;
    
    @ApiModelProperty("每页记录数")
    private Integer pageSize = 10;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("姓名")
    private String name;
    
    @ApiModelProperty("角色ID")
    private Long roleId;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
} 