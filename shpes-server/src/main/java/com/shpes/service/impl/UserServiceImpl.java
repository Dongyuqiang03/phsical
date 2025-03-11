package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.exception.ApiException;
import com.shpes.common.utils.SecurityUtils;
import com.shpes.dto.UserDTO;
import com.shpes.entity.SysUser;
import com.shpes.entity.SysUserRole;
import com.shpes.mapper.SysUserMapper;
import com.shpes.mapper.SysUserRoleMapper;
import com.shpes.service.SysDepartmentService;
import com.shpes.service.SysRoleService;
import com.shpes.service.UserService;
import com.shpes.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDepartmentService departmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CommonPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, String name,
            String phone, Long departmentId, Integer userType, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
                .like(StringUtils.hasText(name), SysUser::getName, name)
                .like(StringUtils.hasText(phone), SysUser::getPhone, phone)
                .eq(departmentId != null, SysUser::getDepartmentId, departmentId)
                .eq(userType != null, SysUser::getUserType, userType)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);

        // 执行分页查询
        Page<SysUser> page = page(new Page<>(pageNum, pageSize), wrapper);
        
        // 转换为VO对象
        List<UserVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserDTO userDTO) {
        // 检查唯一性
        checkUnique(null, userDTO);

        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);
        
        // 设置密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        save(user);

        // 保存用户角色关系
        saveUserRoles(user.getId(), userDTO.getRoleIds());

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long id, UserDTO userDTO) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
        }

        // 检查唯一性
        checkUnique(id, userDTO);

        // 更新用户信息
        BeanUtils.copyProperties(userDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);

        // 更新用户角色关系
        saveUserRoles(id, userDTO.getRoleIds());

        return convertToVO(user);
    }

    @Override
    public UserVO updateStatus(Long id, Integer status) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new ApiException("用户不存在");
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
            throw new ApiException("用户不存在");
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
            throw new ApiException("用户不存在");
        }

        // 重置密码为123456
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public UserVO getCurrentUser() {
        return convertToVO(SecurityUtils.getCurrentUser());
    }

    @Override
    public UserVO updateCurrentUser(UserDTO userDTO) {
        return updateUser(SecurityUtils.getCurrentUserId(), userDTO);
    }

    @Override
    public List<UserVO> getUsersByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDepartmentId, departmentId)
                .eq(SysUser::getStatus, 1);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVO> getUsersByRoleId(Long roleId) {
        // 查询角色下的所有用户ID
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        List<Long> userIds = userRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询用户信息
        return listByIds(userIds).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkIdCardExists(String idCard) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getIdCard, idCard);
        return count(wrapper) > 0;
    }

    /**
     * 检查用户信息唯一性
     */
    private void checkUnique(Long userId, UserDTO userDTO) {
        // 检查用户名
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userDTO.getUsername())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException("用户名已存在");
        }

        // 检查手机号
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, userDTO.getPhone())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException("手机号已存在");
        }

        // 检查邮箱
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, userDTO.getEmail())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException("邮箱已存在");
        }

        // 检查身份证号
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getIdCard, userDTO.getIdCard())
                .ne(userId != null, SysUser::getId, userId);
        if (count(wrapper) > 0) {
            throw new ApiException("身份证号已存在");
        }
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
        if (user.getDepartmentId() != null) {
            userVO.setDepartmentName(departmentService.getDepartmentNameById(user.getDepartmentId()));
        }

        // 设置角色信息
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, user.getId());
        List<Long> roleIds = userRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        userVO.setRoleIds(roleIds);

        // 设置角色名称
        if (!roleIds.isEmpty()) {
            userVO.setRoleNames(roleService.getRoleNamesByIds(roleIds));
        }

        return userVO;
    }
} 