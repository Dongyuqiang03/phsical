package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.SysDepartment;
import com.shpes.annotation.RequiresPermission;
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
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<CommonPage<DepartmentVO>> getDepartmentPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(departmentService.getDepartmentPage(pageNum, pageSize, keyword));
    }

    @ApiOperation("获取所有部门")
    @GetMapping("/all")
    @RequiresPermission(RoleConstants.USER)
    public CommonResult<List<DepartmentVO>> getAllDepartments() {
        return CommonResult.success(departmentService.getAllDepartments());
    }

    @ApiOperation("获取部门详情")
    @GetMapping("/{id}")
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<DepartmentVO> getDepartment(@PathVariable Long id) {
        return CommonResult.success(departmentService.getDepartmentPage(1, 1, null).getRecords().stream()
                .filter(dept -> dept.getId().equals(id))
                .findFirst()
                .orElse(null));
    }

    @ApiOperation("创建部门")
    @PostMapping
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<Void> createDepartment(@Valid @RequestBody SysDepartment department) {
        departmentService.createDepartment(department);
        return CommonResult.success(null);
    }

    @ApiOperation("更新部门")
    @PutMapping("/{id}")
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<Void> updateDepartment(@PathVariable Long id, @Valid @RequestBody SysDepartment department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return CommonResult.success(null);
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新部门状态")
    @PutMapping("/{id}/status")
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<Void> updateDepartmentStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        departmentService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @ApiOperation("获取部门人员列表")
    @GetMapping("/{id}/users")
    @RequiresPermission(RoleConstants.ADMIN)
    public CommonResult<List<UserSimpleVO>> getDepartmentUsers(@PathVariable Long id) {
        return CommonResult.success(departmentService.getDepartmentUsers(id));
    }
} 
