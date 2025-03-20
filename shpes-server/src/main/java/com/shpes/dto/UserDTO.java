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
    
    /**
     * 用于标识创建操作的验证组
     */
    public interface Create {}

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @NotBlank(message = "用户名不能为空", groups = {Create.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名必须为3-20个字符，只能包含字母、数字和下划线")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    
    @NotBlank(message = "姓名不能为空", groups = {Create.class})
    @ApiModelProperty(value = "姓名", required = true)
    private String name;
    
    @NotNull(message = "用户类型不能为空", groups = {Create.class})
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 3, message = "用户类型不正确")
    @ApiModelProperty(value = "用户类型：1-医护人员，2-教职工，3-学生", required = true)
    private Integer userType;
    
    @ApiModelProperty(value = "角色ID列表")
    private List<Long> roleIds;
    
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    private String email;
    
    @NotNull(message = "状态不能为空", groups = {Create.class})
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    @ApiModelProperty(value = "状态：0-禁用，1-启用", required = true)
    private Integer status;
    
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空", groups = {Create.class})
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间", groups = {Create.class})
    private String password;
} 