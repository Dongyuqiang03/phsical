package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.dto.RegisterDTO;
import com.shpes.dto.UserDTO;
import com.shpes.dto.UserQueryDTO;
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
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    CommonPage<UserVO> getUserPage(UserQueryDTO queryDTO);

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
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);

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

}