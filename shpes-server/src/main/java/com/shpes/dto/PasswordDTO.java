package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 密码修改数据传输对象
 */
@Data
@ApiModel(description = "密码修改参数")
public class PasswordDTO {

    @ApiModelProperty(value = "旧密码", required = true)
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", 
            message = "密码必须包含大小写字母和数字，长度在8-20之间")
    private String newPassword;

    @ApiModelProperty(value = "确认新密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
} 