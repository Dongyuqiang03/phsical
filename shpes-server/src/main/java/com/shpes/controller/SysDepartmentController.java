package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.entity.SysDepartment;
import com.shpes.entity.SysUser;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.DepartmentVO;
import com.shpes.vo.UserSimpleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部门管理控制器
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/api/departments")
public class SysDepartmentController {

    @Autowired
    private SysDepartmentService departmentService;

    @ApiOperation("获取部门列表")
    @GetMapping
    @HasPermission("system:department:list")
    public CommonResult<CommonPage<DepartmentVO>> getDepartmentPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(departmentService.getDepartmentPage(pageNum, pageSize, keyword));
    }

    @ApiOperation("获取所有部门")
    @GetMapping("/all")
    @HasPermission("system:department:list")
    public CommonResult<List<DepartmentVO>> getAllDepartments() {
        return CommonResult.success(departmentService.getAllDepartments());
    }

    @ApiOperation("创建部门")
    @PostMapping
    @HasPermission("system:department:create")
    public CommonResult<Void> createDepartment(@Valid @RequestBody SysDepartment department) {
        departmentService.createDepartment(department);
        return CommonResult.success(null);
    }

    @ApiOperation("更新部门")
    @PutMapping("/{id}")
    @HasPermission("system:department:update")
    public CommonResult<Void> updateDepartment(@PathVariable Long id, @Valid @RequestBody SysDepartment department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return CommonResult.success(null);
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    @HasPermission("system:department:delete")
    public CommonResult<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新部门状态")
    @PutMapping("/{id}/status")
    @HasPermission("system:department:update")
    public CommonResult<Void> updateDepartmentStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        departmentService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @ApiOperation("获取部门人员列表")
    @GetMapping("/{id}/users")
    @HasPermission("system:department:list")
    public CommonResult<List<UserSimpleVO>> getDepartmentUsers(@PathVariable Long id) {
        return CommonResult.success(departmentService.getDepartmentUsers(id));
    }
} 