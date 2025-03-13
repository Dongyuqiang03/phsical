package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.dto.UserDTO;
import com.shpes.security.annotation.HasPermission;
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

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping
    @HasPermission("sys:user:list")
    public CommonResult<CommonPage<UserVO>> getUserPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("姓名") @RequestParam(required = false) String name,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("部门ID") @RequestParam(required = false) Long departmentId,
            @ApiParam("用户类型") @RequestParam(required = false) Integer userType,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        return CommonResult.success(userService.getUserPage(pageNum, pageSize, username, name, phone, departmentId, userType, status));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @HasPermission("sys:user:list")
    public CommonResult<UserVO> getUserById(@PathVariable Long id) {
        return CommonResult.success(userService.getUserById(id));
    }

    @ApiOperation("创建用户")
    @PostMapping
    @HasPermission("sys:user:create")
    public CommonResult<UserVO> createUser(@Validated({UserDTO.Create.class, Default.class}) @RequestBody UserDTO userDTO) {
        return CommonResult.success(userService.createUser(userDTO));
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @HasPermission("sys:user:update")
    public CommonResult<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return CommonResult.success(userService.updateUser(id, userDTO));
    }

    @ApiOperation("更新用户状态")
    @PostMapping("/{id}/status")
    @HasPermission("sys:user:update")
    public CommonResult<UserVO> updateUserStatus(
            @PathVariable Long id,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam Integer status) {
        return CommonResult.success(userService.updateStatus(id, status));
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @HasPermission("sys:user:delete")
    public CommonResult<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return CommonResult.success(null);
    }

    @ApiOperation("重置用户密码")
    @PostMapping("/{id}/password/reset")
    @HasPermission("sys:user:update")
    public CommonResult<Void> resetUserPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return CommonResult.success(null);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public CommonResult<UserVO> getCurrentUser() {
        return CommonResult.success(userService.getCurrentUser());
    }

    @ApiOperation("更新当前用户信息")
    @PutMapping("/current")
    public CommonResult<UserVO> updateCurrentUser(@Valid @RequestBody UserDTO userDTO) {
        return CommonResult.success(userService.updateCurrentUser(userDTO));
    }
} 