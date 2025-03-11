package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 用户数据传输对象
 */
@Data
@ApiModel(description = "用户数据传输对象")
public class UserDTO {
    
    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须为4-16位字母、数字或下划线")
    private String username;
    
    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空", groups = {Create.class})
    @Pattern(regexp = "^[a-zA-Z0-9_@#$%^&*]{6,20}$", message = "密码必须为6-20位字母、数字或特殊字符")
    private String password;
    
    @ApiModelProperty("姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @ApiModelProperty("性别：0-未知，1-男，2-女")
    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Integer gender;
    
    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @ApiModelProperty("邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty("身份证号")
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCard;
    
    @ApiModelProperty("出生日期")
    private String birthday;
    
    @ApiModelProperty("头像")
    private String avatar;
    
    @ApiModelProperty("部门ID")
    @NotNull(message = "部门不能为空")
    private Long departmentId;
    
    @ApiModelProperty("用户类型：1-管理员，2-医生，3-护士，4-教职工，5-学生")
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型值不正确")
    @Max(value = 5, message = "用户类型值不正确")
    private Integer userType;
    
    @ApiModelProperty("角色ID列表")
    @NotEmpty(message = "角色不能为空")
    private List<Long> roleIds;
    
    /**
     * 创建校验组
     */
    public interface Create {}
} 