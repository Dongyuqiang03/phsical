package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.dto.RegisterDTO;
import com.shpes.dto.UserDTO;
import com.shpes.entity.SysUser;
import com.shpes.vo.UserVO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * 系统用户管理Service
 */
public interface SysUserService extends IBaseService<SysUser> {

    /**
     * 分页查询用户
     */
    CommonPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, String name, 
            String phone, Long departmentId, Integer userType, Integer status);

    /**
     * 获取用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户
     */
    UserVO createUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    UserVO updateUser(Long id, UserDTO userDTO);

    /**
     * 更新用户状态
     */
    UserVO updateStatus(Long id, Integer status);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     */
    void resetPassword(Long id);

    /**
     * 更新用户密码
     */
    void updatePassword(Long id, String newPassword);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser();

    /**
     * 更新当前用户信息
     */
    UserVO updateCurrentUser(UserDTO userDTO);

    /**
     * 根据部门ID获取用户列表
     */
    List<UserVO> getUsersByDepartmentId(Long departmentId);

    /**
     * 根据角色ID获取用户列表
     */
    List<UserVO> getUsersByRoleId(Long roleId);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据用户名获取用户信息
     */
    UserVO getUserByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean checkEmailExists(String email);

    /**
     * 检查身份证号是否存在
     */
    boolean checkIdCardExists(String idCard);

    /**
     * 根据用户ID获取用户名
     */
    String getUserNameById(Long userId);

    /**
     * 获取用户权限列表
     */
    List<SimpleGrantedAuthority> getUserAuthorities(Long userId);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户角色编码列表
     * 用于前端角色判断
     */
    List<String> getUserRoleCodes(Long userId);

    /**
     * 获取用户权限编码列表
     * 用于权限判断
     */
    List<String> getUserPermissionCodes(Long userId);

    /**
     * 获取用户权限ID列表
     * 用于权限管理
     */
    List<Long> getUserPermissionIds(Long userId);
}