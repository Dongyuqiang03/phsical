package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.entity.ExamPackage;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.ExamPackageService;
import com.shpes.vo.ExamPackageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 体检套餐管理控制器
 */
@Api(tags = "体检套餐管理")
@RestController
@RequestMapping("/api/packages")
public class ExamPackageController {

    @Autowired
    private ExamPackageService packageService;

    @ApiOperation("分页查询体检套餐")
    @GetMapping
    @HasPermission("exam:package:list")
    public CommonResult<CommonPage<ExamPackageVO>> getPackagePage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("套餐名称") @RequestParam(required = false) String name,
            @ApiParam("套餐类型") @RequestParam(required = false) String type,
            @ApiParam("适用性别：0-不限，1-男，2-女") @RequestParam(required = false) Integer gender,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        return CommonResult.success(packageService.getPackagePage(pageNum, pageSize, name, type, gender, status));
    }

    @ApiOperation("获取体检套餐详情")
    @GetMapping("/{id}")
    @HasPermission("exam:package:list")
    public CommonResult<ExamPackageVO> getPackageById(@PathVariable Long id) {
        return CommonResult.success(packageService.getPackageById(id));
    }

    @ApiOperation("获取所有可用套餐")
    @GetMapping("/available")
    public CommonResult<List<ExamPackageVO>> getAvailablePackages(
            @ApiParam("适用性别：0-不限，1-男，2-女") @RequestParam(required = false) Integer gender) {
        return CommonResult.success(packageService.getAvailablePackages(gender));
    }

    @ApiOperation("创建体检套餐")
    @PostMapping
    @HasPermission("exam:package:create")
    public CommonResult<ExamPackageVO> createPackage(@Valid @RequestBody ExamPackage examPackage) {
        return CommonResult.success(packageService.createPackage(examPackage));
    }

    @ApiOperation("更新体检套餐")
    @PutMapping("/{id}")
    @HasPermission("exam:package:update")
    public CommonResult<ExamPackageVO> updatePackage(@PathVariable Long id, @Valid @RequestBody ExamPackage examPackage) {
        examPackage.setId(id);
        return CommonResult.success(packageService.updatePackage(examPackage));
    }

    @ApiOperation("删除体检套餐")
    @DeleteMapping("/{id}")
    @HasPermission("exam:package:delete")
    public CommonResult<Void> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新套餐状态")
    @PutMapping("/{id}/status")
    @HasPermission("exam:package:update")
    public CommonResult<ExamPackageVO> updatePackageStatus(
            @PathVariable Long id,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam Integer status) {
        return CommonResult.success(packageService.updateStatus(id, status));
    }

    @ApiOperation("配置套餐项目")
    @PostMapping("/{id}/items")
    @HasPermission("exam:package:update")
    public CommonResult<ExamPackageVO> configurePackageItems(
            @PathVariable Long id,
            @RequestBody List<Long> itemIds) {
        return CommonResult.success(packageService.configureItems(id, itemIds));
    }

    @ApiOperation("获取套餐项目列表")
    @GetMapping("/{id}/items")
    @HasPermission("exam:package:list")
    public CommonResult<List<ExamItemVO>> getPackageItems(@PathVariable Long id) {
        return CommonResult.success(packageService.getPackageItems(id));
    }

    @ApiOperation("获取套餐统计信息")
    @GetMapping("/stats")
    @HasPermission("exam:package:stats")
    public CommonResult<Object> getPackageStats() {
        return CommonResult.success(packageService.getPackageStats());
    }
} 