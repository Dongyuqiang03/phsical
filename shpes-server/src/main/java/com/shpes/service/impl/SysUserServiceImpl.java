package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.dto.UserCreateDTO;
import com.shpes.dto.UserQueryDTO;
import com.shpes.entity.SysUser;
import com.shpes.entity.SysUserRole;
import com.shpes.mapper.SysRolePermissionMapper;
import com.shpes.mapper.SysUserMapper;
import com.shpes.mapper.SysUserRoleMapper;
import com.shpes.service.SysDepartmentService;
import com.shpes.service.SysRoleService;
import com.shpes.service.SysUserService;
import com.shpes.utils.PasswordUtils;
import com.shpes.vo.RoleVO;
import com.shpes.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDepartmentService departmentService;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;


    @Override
    public CommonPage<UserVO> getUserPage(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUser::getUsername, queryDTO.getUsername())
                .like(StringUtils.hasText(queryDTO.getRealName()), SysUser::getRealName, queryDTO.getRealName())
                .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
                .orderByDesc(SysUser::getCreateTime);

        // 如果指定了角色ID，先查询该角色下的用户ID
        if (queryDTO.getRoleId() != null) {
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getRoleId, queryDTO.getRoleId());
            List<Long> userIds = userRoleMapper.selectList(userRoleWrapper).stream()
                    .map(SysUserRole::getUserId)
                    .collect(Collectors.toList());

            if (userIds.isEmpty()) {
                // 如果没有找到用户，直接返回空结果
                return CommonPage.restPage(new ArrayList<>(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
            }

            wrapper.in(SysUser::getId, userIds);
        }

        // 执行分页查询
        Page<SysUser> page = page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        // 转换为VO对象
        List<UserVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateDTO userCreateDTO) {
        // 检查唯一性
        checkUnique(null, userCreateDTO);

        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userCreateDTO, user);

        // 设置密码
        user.setPassword(org.apache.commons.lang3.StringUtils.isBlank(userCreateDTO.getPassword()) ? PasswordUtils.encode(PasswordUtils.DEFAULT_PASSWORD) : PasswordUtils.encode(userCreateDTO.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        save(user);

        // 保存用户角色关系
//        saveUserRoles(user.getId(), userDTO.getRoleIds());

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long id, UserCreateDTO userCreateDTO) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 检查唯一性
        checkUnique(id, userCreateDTO);

        // 更新用户信息
        BeanUtils.copyProperties(userCreateDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);

        // 更新用户角色关系
        if(!CollectionUtils.isEmpty(userCreateDTO.getRoleIds())){
            saveUserRoles(id, userCreateDTO.getRoleIds());
        }

        return convertToVO(user);
    }

    @Override
    public UserVO updateStatus(Long id, Integer status) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 更新状态
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 检查用户是否存在
        if (!removeById(id)) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 删除用户角色关系
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public void resetPassword(Long id) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 重置密码为123456
        user.setPassword(PasswordUtils.encode("123456"));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return count(wrapper) > 0;
    }

    @Override
    public String getUserNameById(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUser user = getById(userId);
        return user != null ? user.getUsername() : null;
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 更新密码
        user.setPassword(PasswordUtils.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        return userRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserRoleCodes(Long userId) {
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleService.getRoleCodes(roleIds);
    }

    @Override
    public List<SimpleGrantedAuthority> getUserAuthorities(Long userId) {
        // 获取用户的所有权限编码
        List<String> permissionCodes = sysRolePermissionMapper.selectUserPermissionCodes(userId);

        // 转换为Spring Security的权限对象
        return permissionCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 检查用户信息唯一性
     */
    private void checkUnique(Long userId, UserCreateDTO userCreateDTO) {
        // 检查用户名
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userCreateDTO.getUsername())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException(ResultCode.DUPLICATE_USERNAME);
        }

        // 检查手机号
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, userCreateDTO.getPhone())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException(ResultCode.DUPLICATE_USERNAME);
        }

//        // 检查邮箱
//        wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SysUser::getEmail, userDTO.getEmail())
//                .ne(userId != null, SysUser::getId, userId);
//        if (count(wrapper) > 0) {
//            throw new ApiException(ResultCode.DUPLICATE_USERNAME);
//        }
//
//        // 检查身份证号
//        wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SysUser::getIdCard, userDTO.getIdCard())
//                .ne(userId != null, SysUser::getId, userId);
//        if (count(wrapper) > 0) {
//            throw new ApiException(ResultCode.DUPLICATE_USERNAME);
//        }
    }

    /**
     * 保存用户角色关系
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关系
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 保存新的角色关系
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            userRoles.forEach(userRoleMapper::insert);
        }
    }

    /**
     * 将用户实体转换为VO对象
     */
    private UserVO convertToVO(SysUser user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 设置部门名称
        if (user.getDeptId() != null) {
            userVO.setDeptName(departmentService.getDepartmentNameById(user.getDeptId()));
        }

        // 设置角色信息
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, user.getId());
        List<Long> roleIds = userRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        if (!roleIds.isEmpty()) {
            List<RoleVO> roleVOs = roleService.getRoleVOsByIds(roleIds);
            userVO.setRoles(roleVOs);
        }

        return userVO;
    }
}