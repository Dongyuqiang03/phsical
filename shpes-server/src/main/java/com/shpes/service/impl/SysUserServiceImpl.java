package com.shpes.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.system.entity.SysUser;
import com.shpes.system.mapper.SysUserMapper;
import com.shpes.system.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    public Page<SysUser> getUserPage(Page<SysUser> page, String username, String realName, Long roleId, Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                .like(StringUtils.isNotBlank(realName), SysUser::getRealName, realName)
                .eq(roleId != null, SysUser::getRoleId, roleId)
                .eq(deptId != null, SysUser::getDeptId, deptId)
                .orderByDesc(SysUser::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(SysUser user) {
        // 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            throw new ApiException(ResultCode.USERNAME_EXISTS);
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        baseMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        // 检查用户是否存在
        SysUser existingUser = baseMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND);
        }
        
        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) && isUsernameExists(user.getUsername())) {
            throw new ApiException(ResultCode.USERNAME_EXISTS);
        }
        
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUser(Long[] ids) {
        for (Long id : ids) {
            deleteUser(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserPassword(Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(SysUser user) {
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ApiException(ResultCode.OLD_PASSWORD_ERROR);
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        baseMapper.updateById(user);
    }

    /**
     * 检查用户名是否已存在
     */
    private boolean isUsernameExists(String username) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)) > 0;
    }
} 