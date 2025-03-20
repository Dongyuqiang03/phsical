package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.dto.UserDTO;
import com.shpes.dto.UserQueryDTO;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.SysUserService;
import com.shpes.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理控制器 - 处理系统用户管理相关操作
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @ApiOperation(value = "分页查询用户", notes = "支持按用户名、姓名、角色和状态筛选")
    @GetMapping("/list")
    @RequiresPermission("system:user")
    public CommonResult<CommonPage<UserVO>> getUserPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("姓名") @RequestParam(required = false) String name,
            @ApiParam("角色ID") @RequestParam(required = false) Long roleId,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUsername(username);
        queryDTO.setName(name);
        queryDTO.setRoleId(roleId);
        queryDTO.setStatus(status);
        
        return CommonResult.success(userService.getUserPage(queryDTO));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:user")
    public CommonResult<UserVO> getUserById(@PathVariable Long id) {
        return CommonResult.success(userService.getUserById(id));
    }

    @ApiOperation("创建用户")
    @PostMapping
    @RequiresPermission("system:user")
    public CommonResult<UserVO> createUser(@Validated({UserDTO.Create.class, Default.class}) @RequestBody UserDTO userDTO) {
        // 根据用户类型设置默认角色
        if (userDTO.getUserType() != null) {
            if (userDTO.getRoleIds() == null || userDTO.getRoleIds().isEmpty()) {
                List<Long> defaultRoles = new ArrayList<>();
                if (userDTO.getUserType() == 1) {
                    // 医护人员默认设置为医护角色
                    defaultRoles.add(RoleConstants.ROLE_MEDICAL_ID);
                } else {
                    // 教职工和学生默认设置为普通用户角色
                    defaultRoles.add(RoleConstants.ROLE_USER_ID);
                }
                userDTO.setRoleIds(defaultRoles);
            }
        }
        
        return CommonResult.success(userService.createUser(userDTO));
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @RequiresPermission("system:user")
    public CommonResult<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        // 验证角色是否符合用户类型
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            boolean isValid = validateUserTypeRoles(userDTO.getUserType(), userDTO.getRoleIds());
            if (!isValid) {
                return CommonResult.failed("所选角色与用户类型不匹配");
            }
        }
        
        return CommonResult.success(userService.updateUser(id, userDTO));
    }

    // 添加用户类型与角色验证的私有方法
    private boolean validateUserTypeRoles(Integer userType, List<Long> roleIds) {
        if (userType == null || roleIds == null) {
            return true;
        }
        
        switch (userType) {
            case 1: // 医护人员
                return roleIds.stream().anyMatch(id -> 
                    id.equals(RoleConstants.ROLE_MEDICAL_ID) || 
                    id.equals(RoleConstants.ROLE_ADMIN_ID));
            case 2: // 教职工
            case 3: // 学生
                return roleIds.stream().anyMatch(id -> 
                    id.equals(RoleConstants.ROLE_USER_ID));
            default:
                return false;
        }
    }

    @ApiOperation("更新用户状态")
    @PostMapping("/{id}/status")
    @RequiresPermission("system:user")
    public CommonResult<UserVO> updateUserStatus(
            @PathVariable Long id,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam Integer status) {
        return CommonResult.success(userService.updateStatus(id, status));
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:user")
    public CommonResult<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return CommonResult.success(null);
    }

    @ApiOperation("重置用户密码")
    @PostMapping("/{id}/password/reset")
    @RequiresPermission("system:user")
    public CommonResult<Void> resetUserPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新用户密码")
    @PostMapping("/{id}/password")
    @RequiresPermission("system:user")
    public CommonResult<Void> updateUserPassword(
            @PathVariable Long id,
            @ApiParam("新密码") @RequestParam String newPassword) {
        userService.updatePassword(id, newPassword);
        return CommonResult.success(null);
    }

    @ApiOperation("根据部门ID获取用户列表")
    @GetMapping("/department/{departmentId}")
    @RequiresPermission("system:user")
    public CommonResult<List<UserVO>> getUsersByDepartmentId(@PathVariable Long departmentId) {
        return CommonResult.success(userService.getUsersByDepartmentId(departmentId));
    }

    @ApiOperation("根据角色ID获取用户列表")
    @GetMapping("/role/{roleId}")
    @RequiresPermission("system:user")
    public CommonResult<List<UserVO>> getUsersByRoleId(@PathVariable Long roleId) {
        return CommonResult.success(userService.getUsersByRoleId(roleId));
    }

    @ApiOperation("根据用户名获取用户信息")
    @GetMapping("/username/{username}")
    @RequiresPermission("system:user")
    public CommonResult<UserVO> getUserByUsername(@PathVariable String username) {
        return CommonResult.success(userService.getUserByUsername(username));
    }

    @ApiOperation("检查用户名是否存在")
    @GetMapping("/check/username")
    public CommonResult<Boolean> checkUsernameExists(@RequestParam String username) {
        return CommonResult.success(userService.checkUsernameExists(username));
    }

    @ApiOperation("检查手机号是否存在")
    @GetMapping("/check/phone")
    public CommonResult<Boolean> checkPhoneExists(@RequestParam String phone) {
        return CommonResult.success(userService.checkPhoneExists(phone));
    }

    @ApiOperation("检查邮箱是否存在")
    @GetMapping("/check/email")
    public CommonResult<Boolean> checkEmailExists(@RequestParam String email) {
        return CommonResult.success(userService.checkEmailExists(email));
    }

    @ApiOperation("检查身份证号是否存在")
    @GetMapping("/check/idcard")
    public CommonResult<Boolean> checkIdCardExists(@RequestParam String idCard) {
        return CommonResult.success(userService.checkIdCardExists(idCard));
    }

    @ApiOperation("根据用户ID获取用户名")
    @GetMapping("/{id}/username")
    public CommonResult<String> getUserNameById(@PathVariable Long id) {
        return CommonResult.success(userService.getUserNameById(id));
    }

    @ApiOperation("获取用户角色编码列表")
    @GetMapping("/{id}/role-codes")
    public CommonResult<List<String>> getUserRoleCodes(@PathVariable Long id) {
        return CommonResult.success(userService.getUserRoleCodes(id));
    }

    @ApiOperation("获取用户权限编码列表")
    @GetMapping("/{id}/permission-codes")
    public CommonResult<List<String>> getUserPermissionCodes(@PathVariable Long id) {
        return CommonResult.success(userService.getUserPermissionCodes(id));
    }
} 
