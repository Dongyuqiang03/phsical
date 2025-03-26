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
            @ApiParam("姓名") @RequestParam(required = false) String realName,
            @ApiParam("角色ID") @RequestParam(required = false) Long roleId,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUsername(username);
        queryDTO.setRealName(realName);
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
        return CommonResult.success(userService.createUser(userDTO));
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @RequiresPermission("system:user")
    public CommonResult<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return CommonResult.success(userService.updateUser(id, userDTO));
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
} 
