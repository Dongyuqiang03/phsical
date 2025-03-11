package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 */
@Data
@ApiModel("登录请求")
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String captchaCode;

    @NotBlank(message = "验证码key不能为空")
    @ApiModelProperty("验证码key")
    private String captchaKey;
} 