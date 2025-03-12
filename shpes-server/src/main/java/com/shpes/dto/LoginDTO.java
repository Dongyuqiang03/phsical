package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求数据传输对象
 */
@Data
@ApiModel(description = "登录请求参数")
public class LoginDTO {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    @ApiModelProperty("验证码key")
    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
} 