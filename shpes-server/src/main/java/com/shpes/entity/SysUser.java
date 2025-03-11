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

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("部门ID")
    private Long departmentId;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("最后登录时间")
    private Long lastLoginTime;

    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;

    private String idCard;

    private Integer gender;

    private Integer userType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}