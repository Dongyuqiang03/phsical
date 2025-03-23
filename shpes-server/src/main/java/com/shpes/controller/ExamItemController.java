package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.ExamItem;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamItemService;
import com.shpes.vo.ExamItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 体检项目管理控制器
 */
@Api(tags = "体检项目管理")
@RestController
@RequestMapping("/exam/items")
public class ExamItemController {

    @Autowired
    private ExamItemService itemService;

    @ApiOperation("获取项目列表")
    @GetMapping("/list")
    @RequiresPermission("exam:item")
    public CommonResult<CommonPage<ExamItemVO>> getItemPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return CommonResult.success(itemService.getItemPage(pageNum, pageSize, keyword));
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/{id}")
    @RequiresPermission("exam:item")
    public CommonResult<ExamItem> getItem(@PathVariable Long id) {
        return CommonResult.success(itemService.getItem(id));
    }

    @ApiOperation("创建项目")
    @PostMapping
    @RequiresPermission("exam:item")
    public CommonResult<Void> createItem(@Valid @RequestBody ExamItem item) {
        itemService.createItem(item);
        return CommonResult.success(null);
    }

    @ApiOperation("更新项目")
    @PutMapping("/{id}")
    @RequiresPermission("exam:item")
    public CommonResult<Void> updateItem(@PathVariable Long id, @Valid @RequestBody ExamItem item) {
        item.setId(id);
        itemService.updateItem(item);
        return CommonResult.success(null);
    }

    @ApiOperation("删除项目")
    @DeleteMapping("/{id}")
    @RequiresPermission("exam:item")
    public CommonResult<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新项目状态")
    @PutMapping("/{id}/status")
    @RequiresPermission("exam:item")
    public CommonResult<Void> updateItemStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        itemService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @ApiOperation("获取项目参考值")
    @GetMapping("/{id}/reference")
    @RequiresPermission("exam:item")
    public CommonResult<ExamItemVO> getItemReference(@PathVariable Long id) {
        return CommonResult.success(itemService.getItemReference(id));
    }

    @ApiOperation("获取所有可用体检项目")
    @GetMapping("/available")
    @RequiresPermission("exam:item")
    public CommonResult<List<ExamItemVO>> getAvailableItems() {
        return CommonResult.success(itemService.getAvailableItems());
    }
}
