package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.SysRole;
import com.shpes.entity.SysRolePermission;
import com.shpes.entity.SysUserRole;
import com.shpes.mapper.SysRoleMapper;
import com.shpes.mapper.SysRolePermissionMapper;
import com.shpes.mapper.SysUserRoleMapper;
import com.shpes.service.SysRoleService;
import com.shpes.vo.RoleDetailVO;
import com.shpes.vo.RoleVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public CommonPage<RoleDetailVO> getRolePage(Integer pageNum, Integer pageSize, String name, Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), SysRole::getRoleName, name)
                .eq(status != null, SysRole::getStatus, status)
                .orderByAsc(SysRole::getId);
        Page<SysRole> rolePage = baseMapper.selectPage(page, wrapper);
        
        List<RoleDetailVO> records = rolePage.getRecords().stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
        
        return CommonPage.restPage(records, rolePage.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role) {
        // 检查角色名称是否已存在
        if (isRoleNameExists(role.getRoleName())) {
            throw new ApiException(ResultCode.ROLE_NAME_EXISTS);
        }
        
        // 检查角色编码是否已存在
        if (isRoleCodeExists(role.getRoleCode())) {
            throw new ApiException(ResultCode.ROLE_CODE_EXISTS);
        }
        
        baseMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {
        // 检查角色是否存在
        SysRole existingRole = baseMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new ApiException(ResultCode.ROLE_NOT_FOUND);
        }
        
        // 如果修改了角色名称，检查新名称是否已存在
        if (!existingRole.getRoleName().equals(role.getRoleName()) && isRoleNameExists(role.getRoleName())) {
            throw new ApiException(ResultCode.ROLE_NAME_EXISTS);
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (!existingRole.getRoleCode().equals(role.getRoleCode()) && isRoleCodeExists(role.getRoleCode())) {
            throw new ApiException(ResultCode.ROLE_CODE_EXISTS);
        }
        
        baseMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 删除角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));
        
        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, id));
        
        // 删除角色
        baseMapper.deleteById(id);
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<SysRole> roles = baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getId));
        
        return roles.stream()
                .map(RoleVO::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有的角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));
        
        // 添加新的角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            
            rolePermissions.forEach(rolePermissionMapper::insert);
        }
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId));
        
        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleVO> getUserRoles(Long userId) {
        List<SysRole> roles = userRoleMapper.selectRoleDetailsByUserId(userId);
        return roles.stream()
                .map(RoleVO::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
                        .select(SysRolePermission::getPermissionId))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRolePermissionCodes(Long roleId) {
        // 获取权限ID列表
        List<Long> permissionIds = getRolePermissionIds(roleId);
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询权限编码
        return baseMapper.selectPermissionCodesByIds(permissionIds);
    }

    @Override
    public List<String> getRoleCodes(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询指定ID的角色列表
        List<SysRole> roles = baseMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getId));

        // 提取角色编码
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleVO> getRoleVOsByIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询指定ID的角色列表
        List<SysRole> roles = baseMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .orderByAsc(SysRole::getId));

        // 转换为VO对象
        return roles.stream()
                .map(RoleVO::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDetailVO getRoleById(Long id) {
        return RoleDetailVO.convertToVO(baseMapper.selectById(id));
    }

    /**
     * 检查角色名称是否已存在
     */
    private boolean isRoleNameExists(String name) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, name)) > 0;
    }

    /**
     * 检查角色编码是否已存在
     */
    private boolean isRoleCodeExists(String code) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, code)) > 0;
    }

    private RoleDetailVO convertToDetailVO(SysRole role) {
        if (role == null) {
            return null;
        }
        RoleDetailVO vo = new RoleDetailVO();
        vo.setId(role.getId());
        vo.setName(role.getRoleName());
        vo.setCode(role.getRoleCode());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}