package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysRole;
import com.shpes.entity.SysUserDetail;
import com.shpes.entity.SysUserRole;
import com.shpes.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 获取用户的角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取角色的用户ID列表
     */
    @Select("SELECT user_id FROM sys_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 获取用户的角色详细信息
     */
    List<RoleVO> selectRoleDetailsByUserId(@Param("userId") Long userId);

    /**
     * 获取角色的用户详细信息
     */
    List<SysUserDetail> selectUserDetailsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关系
     */
    int batchInsert(@Param("list") List<SysUserRole> userRoles);

    /**
     * 根据条件删除用户角色关系
     */
    int deleteByCondition(@Param("userId") Long userId, @Param("roleId") Long roleId);
}