package com.shpes.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.common.api.CommonResult;
import com.shpes.entity.SysDepartment;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.DepartmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class SysDepartmentController {

    private final SysDepartmentService departmentService;

    @ApiOperation("分页查询部门列表")
    @GetMapping("/list")
    public CommonResult<Page<DepartmentVO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("部门名称") @RequestParam(required = false) String deptName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<SysDepartment> page = new Page<>(pageNum, pageSize);
        return CommonResult.success(departmentService.getDepartmentPage(page, deptName, status));
    }

    @ApiOperation("获取部门列表")
    @GetMapping("/all")
    public CommonResult<List<DepartmentVO>> all() {
        return CommonResult.success(departmentService.getDepartmentList());
    }

    @ApiOperation("创建部门")
    @PostMapping("/create")
    public CommonResult<Boolean> create(@Validated @RequestBody SysDepartment department) {
        return CommonResult.success(departmentService.createDepartment(department));
    }

    @ApiOperation("更新部门")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@Validated @RequestBody SysDepartment department) {
        return CommonResult.success(departmentService.updateDepartment(department));
    }

    @ApiOperation("删除部门")
    @PostMapping("/delete/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        return CommonResult.success(departmentService.deleteDepartment(id));
    }

    @ApiOperation("批量更新部门状态")
    @PostMapping("/batch-update-status")
    public CommonResult<Boolean> batchUpdateStatus(
            @ApiParam("部门ID列表") @RequestParam List<Long> ids,
            @ApiParam("状态") @RequestParam Integer status) {
        return CommonResult.success(departmentService.batchUpdateStatus(ids, status));
    }
} 
