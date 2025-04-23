package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.annotation.RequiresPermission;
import com.shpes.entity.SysRole;
import com.shpes.service.SysRoleService;
import com.shpes.vo.PermissionTreeVO;
import com.shpes.vo.RoleDetailVO;
import com.shpes.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @ApiOperation("分页查询角色")
    @GetMapping("/list")
    @RequiresPermission("system:role")
    public CommonResult<CommonPage<RoleDetailVO>> getRolePage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("角色名称") @RequestParam(required = false) String name,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        return CommonResult.success(roleService.getRolePage(pageNum, pageSize, name, status));
    }

    @ApiOperation("获取所有角色（用于下拉选择）")
    @GetMapping("/all")
    @RequiresPermission("system:role")
    public CommonResult<List<RoleVO>> getAllRoles() {
        return CommonResult.success(roleService.getAllRoles());
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:role")
    public CommonResult<RoleDetailVO> getRoleById(@PathVariable Long id) {
        return CommonResult.success(roleService.getRoleById(id));
    }

    @ApiOperation("创建角色")
    @PostMapping
    @RequiresPermission("system:role")
    public CommonResult<Void> createRole(@Valid @RequestBody SysRole role) {
        roleService.createRole(role);
        return CommonResult.success(null);
    }

    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @RequiresPermission("system:role")
    public CommonResult<Void> updateRole(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateRole(role);
        return CommonResult.success(null);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:role")
    public CommonResult<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return CommonResult.success(null);
    }

    @ApiOperation("分配角色权限")
    @PostMapping("/{roleId}/permissions")
    @RequiresPermission("system:role")
    public CommonResult<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return CommonResult.success(null);
    }

    @ApiOperation("获取角色权限")
    @GetMapping("/{roleId}/permissions")
    @RequiresPermission("system:role")
    public CommonResult<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return CommonResult.success(roleService.getRolePermissions(roleId));
    }

    @ApiOperation("获取用户角色列表")
    @GetMapping("/user/{userId}")
    @RequiresPermission("system:role")
    public CommonResult<List<RoleVO>> getUserRoles(@PathVariable Long userId) {
        return CommonResult.success(roleService.getUserRoles(userId));
    }

    @ApiOperation("获取角色权限编码列表")
    @GetMapping("/{roleId}/permission-codes")
    @RequiresPermission("system:role")
    public CommonResult<List<String>> getRolePermissionCodes(@PathVariable Long roleId) {
        return CommonResult.success(roleService.getRolePermissionCodes(roleId));
    }

    @ApiOperation("获取角色编码列表")
    @PostMapping("/codes")
    @RequiresPermission("system:role")
    public CommonResult<List<String>> getRoleCodes(@RequestBody List<Long> roleIds) {
        return CommonResult.success(roleService.getRoleCodes(roleIds));
    }

    @ApiOperation("获取权限树")
    @GetMapping("/permission-tree")
    @RequiresPermission("system:role")
    public CommonResult<List<PermissionTreeVO>> getPermissionTree() {
        return CommonResult.success(roleService.getPermissionTree());
    }
}
