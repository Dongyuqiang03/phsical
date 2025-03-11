package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.enums.ExamCategoryEnum;
import com.shpes.entity.ExamItem;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.ExamItemService;
import com.shpes.vo.ExamItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * 体检项目管理控制器
 */
@Api(tags = "体检项目管理")
@RestController
@RequestMapping("/api/items")
public class ExamItemController {

    @Autowired
    private ExamItemService itemService;

    @ApiOperation("获取项目列表")
    @GetMapping
    @HasPermission("exam:item:list")
    public CommonResult<CommonPage<ExamItemVO>> getItemPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(itemService.getItemPage(pageNum, pageSize, keyword));
    }

    @ApiOperation("创建体检项目")
    @PostMapping
    @HasPermission("exam:item:create")
    public CommonResult<Void> createItem(@Valid @RequestBody ExamItem item) {
        itemService.createItem(item);
        return CommonResult.success(null);
    }

    @ApiOperation("更新项目信息")
    @PutMapping("/{id}")
    @HasPermission("exam:item:update")
    public CommonResult<Void> updateItem(@PathVariable Long id, @Valid @RequestBody ExamItem item) {
        item.setId(id);
        itemService.updateItem(item);
        return CommonResult.success(null);
    }

    @ApiOperation("获取项目分类")
    @GetMapping("/categories")
    public CommonResult<List<ExamCategoryEnum>> getCategories() {
        return CommonResult.success(Arrays.asList(ExamCategoryEnum.values()));
    }

    @ApiOperation("获取项目参考值")
    @GetMapping("/{id}/reference")
    @HasPermission("exam:item:list")
    public CommonResult<ExamItemVO> getItemReference(@PathVariable Long id) {
        return CommonResult.success(itemService.getItemReference(id));
    }

    @ApiOperation("更新项目状态")
    @PutMapping("/{id}/status")
    @HasPermission("exam:item:update")
    public CommonResult<Void> updateItemStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        itemService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @ApiOperation("删除体检项目")
    @DeleteMapping("/{id}")
    @HasPermission("exam:item:delete")
    public CommonResult<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return CommonResult.success(null);
    }
}