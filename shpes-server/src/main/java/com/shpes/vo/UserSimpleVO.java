package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户简单返回值对象
 */
@Data
@ApiModel(description = "用户简单返回值对象")
public class UserSimpleVO {
    
    @ApiModelProperty("用户ID")
    private Long id;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("姓名")
    private String name;
    
    @ApiModelProperty("手机号")
    private String phone;
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
} 