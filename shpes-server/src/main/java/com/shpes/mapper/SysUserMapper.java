package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysUser;
import com.shpes.entity.SysUserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 获取用户详情（包含部门和角色信息）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    SysUserDetail getUserDetail(@Param("id") Long id);
}