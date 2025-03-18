package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户详情实体类，用于接收数据库查询结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDetail extends SysUser {
    
    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 角色名称列表
     */
    @TableField(exist = false)
    private List<String> roleNames;
}