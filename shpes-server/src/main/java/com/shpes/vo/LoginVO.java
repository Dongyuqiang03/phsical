package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 登录返回值对象
 */
@Data
@ApiModel(description = "登录返回值对象")
public class LoginVO {
    
    @ApiModelProperty("访问令牌")
    private String token;
    
    @ApiModelProperty("用户ID")
    private Long userId;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("真实姓名")
    private String realName;
    
    @ApiModelProperty("角色列表")
    private List<String> roles;
    
    @ApiModelProperty("权限列表")
    private List<String> permissions;
} 