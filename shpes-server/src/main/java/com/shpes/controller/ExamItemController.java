package com.shpes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shpes.common.Result;
import com.shpes.entity.ExamItem;
import com.shpes.service.ExamItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "体检项目管理接口")
@RestController
@RequestMapping("/exam/item")
public class ExamItemController {

    @Autowired
    private ExamItemService examItemService;

    @ApiOperation("分页查询体检项目")
    @GetMapping("/page")
    public Result<IPage<ExamItem>> getExamItemPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) Long categoryId) {
        IPage<ExamItem> pageResult = examItemService.getExamItemPage(page, size, itemName, categoryId);
        return Result.success(pageResult);
    }

    @ApiOperation("添加体检项目")
    @PostMapping
    public Result<Boolean> addExamItem(@RequestBody ExamItem examItem) {
        boolean success = examItemService.addExamItem(examItem);
        return Result.success(success);
    }

    @ApiOperation("更新体检项目")
    @PutMapping
    public Result<Boolean> updateExamItem(@RequestBody ExamItem examItem) {
        boolean success = examItemService.updateExamItem(examItem);
        return Result.success(success);
    }

    @ApiOperation("删除体检项目")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteExamItem(@PathVariable Long id) {
        boolean success = examItemService.deleteExamItem(id);
        return Result.success(success);
    }

    @ApiOperation("获取体检项目详情")
    @GetMapping("/{id}")
    public Result<ExamItem> getExamItemById(@PathVariable Long id) {
        ExamItem examItem = examItemService.getExamItemById(id);
        return Result.success(examItem);
    }
}