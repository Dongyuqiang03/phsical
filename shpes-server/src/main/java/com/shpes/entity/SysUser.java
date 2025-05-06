package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@ApiModel("系统用户")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户编号(学号/工号/医护编号)")
    private String userCode;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("性别(0:女 1:男)")
    private Integer gender;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("用户类型(1:医护人员 2:教职工 3:学生)")
    private Integer userType;

    @ApiModelProperty("状态(0:禁用 1:启用)")
    private Integer status;

    public boolean isEnabled() {
        return status != null && status == 1;
    }
}