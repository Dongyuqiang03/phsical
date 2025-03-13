package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.security.annotation.HasPermission;
import com.shpes.entity.SysRole;
import com.shpes.service.SysRoleService;
import com.shpes.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @ApiOperation("获取角色列表")
    @GetMapping
    @HasPermission("system:role:list")
    public CommonResult<CommonPage<RoleVO>> getRolePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(roleService.getRolePage(pageNum, pageSize, keyword));
    }

    @ApiOperation("创建角色")
    @PostMapping
    @HasPermission("system:role:create")
    public CommonResult<Void> createRole(@Valid @RequestBody SysRole role) {
        roleService.createRole(role);
        return CommonResult.success(null);
    }

    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @HasPermission("system:role:update")
    public CommonResult<Void> updateRole(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateRole(role);
        return CommonResult.success(null);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @HasPermission("system:role:delete")
    public CommonResult<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return CommonResult.success(null);
    }

    @ApiOperation("获取所有角色")
    @GetMapping("/all")
    @HasPermission("system:role:list")
    public CommonResult<List<RoleVO>> getAllRoles() {
        return CommonResult.success(roleService.getAllRoles());
    }

    @ApiOperation("分配角色权限")
    @PostMapping("/{roleId}/permissions")
    @HasPermission("system:role:permission")
    public CommonResult<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return CommonResult.success(null);
    }

    @ApiOperation("获取角色权限")
    @GetMapping("/{roleId}/permissions")
    @HasPermission("system:role:permission")
    public CommonResult<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return CommonResult.success(roleService.getRolePermissions(roleId));
    }
} 