package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录返回结果
 */
@Data
@ApiModel(description = "登录返回结果")
public class LoginVO {
    @ApiModelProperty("访问令牌")
    private String token;

    @ApiModelProperty("用户信息")
    private UserVO user;
} 