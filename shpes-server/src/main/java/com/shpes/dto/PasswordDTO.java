package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求DTO
 */
@Data
@ApiModel("修改密码请求")
public class PasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty("旧密码")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @ApiModelProperty("新密码")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty("确认密码")
    private String confirmPassword;
} 