package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.service.ExamItemCategoryService;
import com.shpes.vo.ExamItemCategoryVO;
import com.shpes.annotation.RequiresPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 体检项目分类管理控制器 - 处理体检项目分类相关操作
 */
@Api(tags = "体检项目分类管理")
@RestController
@RequestMapping("/exam/category")
public class ExamItemCategoryController {

    @Autowired
    private ExamItemCategoryService categoryService;

    @ApiOperation(value = "分页查询分类", notes = "支持按分类名称、编码和状态筛选")
    @GetMapping("/page")
    @RequiresPermission("exam:category")
    public CommonResult<CommonPage<ExamItemCategoryVO>> getCategoryPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类名称") @RequestParam(required = false) String name,
            @ApiParam("分类编码") @RequestParam(required = false) String code,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {
        return CommonResult.success(categoryService.getCategoryPage(pageNum, pageSize, name, code, status));
    }

    @ApiOperation("获取所有分类")
    @GetMapping("/list")
    @RequiresPermission("exam:category")
    public CommonResult<List<ExamItemCategoryVO>> getAllCategories() {
        List<ExamItemCategoryVO> categories = categoryService.getAllCategories();
        return CommonResult.success(categories);
    }

    @ApiOperation("获取指定分类")
    @GetMapping("/{id}")
    @RequiresPermission("exam:category")
    public CommonResult<ExamItemCategoryVO> getCategoryById(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        ExamItemCategoryVO category = categoryService.getCategoryById(id);
        return category != null ? CommonResult.success(category) : CommonResult.failed("分类不存在");
    }

    @ApiOperation("创建分类")
    @PostMapping
    @RequiresPermission("exam:category")
    public CommonResult<Boolean> createCategory(
            @ApiParam(value = "分类信息", required = true) @RequestBody ExamItemCategoryVO categoryVO) {
        boolean success = categoryService.createCategory(categoryVO);
        return success ? CommonResult.success(true) : CommonResult.failed("创建分类失败");
    }

    @ApiOperation("更新分类")
    @PutMapping
    @RequiresPermission("exam:category")
    public CommonResult<Boolean> updateCategory(
            @ApiParam(value = "分类信息", required = true) @RequestBody ExamItemCategoryVO categoryVO) {
        boolean success = categoryService.updateCategory(categoryVO);
        return success ? CommonResult.success(true) : CommonResult.failed("更新分类失败");
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    @RequiresPermission("exam:category")
    public CommonResult<Boolean> deleteCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        return success ? CommonResult.success(true) : CommonResult.failed("删除分类失败");
    }
} 