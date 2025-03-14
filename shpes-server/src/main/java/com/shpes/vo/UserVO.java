package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息返回对象
 */
@Data
@ApiModel(description = "用户信息")
public class UserVO {
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

    @ApiModelProperty("部门ID")
    private Long departmentId;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty("用户类型")
    private Integer userType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("角色列表")
    private List<String> roles;

    @ApiModelProperty("权限列表")
    private List<String> permissions;

    @ApiModelProperty("密码")
    private String password;
    
    @ApiModelProperty("性别（0-未知 1-男 2-女）")
    private Integer gender;
    
    @ApiModelProperty("身份证号")
    private String idCard;
    
    @ApiModelProperty("生日")
    private String birthday;
    
    @ApiModelProperty("头像")
    private String avatar;
    
    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;
    
    @ApiModelProperty("角色名称列表")
    private List<String> roleNames;
    
    @ApiModelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;
    
    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    public boolean isEnabled() {
        return status != null && status == 1;
    }
} 