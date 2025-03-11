package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户返回值对象
 */
@Data
@ApiModel(description = "用户返回值对象")
public class UserVO {
    
    @ApiModelProperty("用户ID")
    private Long id;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("姓名")
    private String name;
    
    @ApiModelProperty("性别：0-未知，1-男，2-女")
    private Integer gender;
    
    @ApiModelProperty("手机号")
    private String phone;
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("身份证号")
    private String idCard;
    
    @ApiModelProperty("出生日期")
    private String birthday;
    
    @ApiModelProperty("头像")
    private String avatar;
    
    @ApiModelProperty("部门ID")
    private Long departmentId;
    
    @ApiModelProperty("部门名称")
    private String departmentName;
    
    @ApiModelProperty("用户类型：1-管理员，2-医生，3-护士，4-教职工，5-学生")
    private Integer userType;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
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
} 