package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.SysRole;
import com.shpes.entity.SysRolePermission;
import com.shpes.entity.SysUserRole;
import com.shpes.mapper.SysRoleMapper;
import com.shpes.mapper.SysRolePermissionMapper;
import com.shpes.mapper.SysUserRoleMapper;
import com.shpes.service.SysRoleService;
import com.shpes.vo.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public CommonPage<RoleVO> getRolePage(Integer pageNum, Integer pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(keyword), SysRole::getName, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), SysRole::getCode, keyword)
                .orderByDesc(SysRole::getCreateTime);

        // 执行分页查询
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectPage(page, wrapper);

        // 转换记录列表
        List<RoleVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 使用通用分页对象，使用一致的命名
        return CommonPage.restPage(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role) {
        // 检查角色名称是否已存在
        if (isRoleNameExists(role.getName())) {
            throw new ApiException(ResultCode.ROLE_NAME_EXISTS);
        }
        
        // 检查角色编码是否已存在
        if (isRoleCodeExists(role.getCode())) {
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
        if (!existingRole.getName().equals(role.getName()) && isRoleNameExists(role.getName())) {
            throw new ApiException(ResultCode.ROLE_NAME_EXISTS);
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (!existingRole.getCode().equals(role.getCode()) && isRoleCodeExists(role.getCode())) {
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
                .orderByAsc(SysRole::getSort));
        
        return roles.stream()
                .map(this::convertToVO)
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

    /**
     * 检查角色名称是否已存在
     */
    private boolean isRoleNameExists(String name) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getName, name)) > 0;
    }

    /**
     * 检查角色编码是否已存在
     */
    private boolean isRoleCodeExists(String code) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, code)) > 0;
    }

    /**
     * 将实体对象转换为VO对象
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        // 获取角色权限
        vo.setPermissionIds(getRolePermissions(role.getId()));
        return vo;
    }
} 