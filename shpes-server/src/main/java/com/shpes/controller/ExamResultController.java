package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.entity.ExamResult;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.ExamResultService;
import com.shpes.vo.ExamResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 体检结果管理控制器
 */
@Api(tags = "体检结果管理")
@RestController
@RequestMapping("/api/results")
public class ExamResultController {

    @Autowired
    private ExamResultService resultService;

    @ApiOperation("获取体检结果列表")
    @GetMapping("/record/{recordId}")
    @HasPermission("exam:result:list")
    public CommonResult<List<ExamResultVO>> getResultsByRecordId(@PathVariable Long recordId) {
        return CommonResult.success(resultService.getResultsByRecordId(recordId));
    }

    @ApiOperation("分页查询体检结果")
    @GetMapping
    @HasPermission("exam:result:list")
    public CommonResult<CommonPage<ExamResultVO>> getResultPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("体检记录ID") @RequestParam(required = false) Long recordId,
            @ApiParam("体检项目ID") @RequestParam(required = false) Long itemId,
            @ApiParam("结果类型：0-正常，1-异常") @RequestParam(required = false) Integer resultType,
            @ApiParam("复核状态：0-待复核，1-已复核") @RequestParam(required = false) Integer reviewStatus) {
        return CommonResult.success(resultService.getResultPage(pageNum, pageSize, recordId, itemId, resultType, reviewStatus));
    }

    @ApiOperation("录入体检结果")
    @PostMapping
    @HasPermission("exam:result:create")
    public CommonResult<ExamResultVO> createResult(@Valid @RequestBody ExamResult result) {
        return CommonResult.success(resultService.createResult(result));
    }

    @ApiOperation("批量录入体检结果")
    @PostMapping("/batch")
    @HasPermission("exam:result:create")
    public CommonResult<List<ExamResultVO>> createResults(@Valid @RequestBody List<ExamResult> results) {
        return CommonResult.success(resultService.createResults(results));
    }

    @ApiOperation("更新体检结果")
    @PutMapping("/{id}")
    @HasPermission("exam:result:update")
    public CommonResult<ExamResultVO> updateResult(@PathVariable Long id, @Valid @RequestBody ExamResult result) {
        result.setId(id);
        return CommonResult.success(resultService.updateResult(result));
    }

    @ApiOperation("删除体检结果")
    @DeleteMapping("/{id}")
    @HasPermission("exam:result:delete")
    public CommonResult<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return CommonResult.success(null);
    }

    @ApiOperation("复核体检结果")
    @PostMapping("/{id}/review")
    @HasPermission("exam:result:review")
    public CommonResult<ExamResultVO> reviewResult(
            @PathVariable Long id,
            @RequestParam(required = false) String suggestion) {
        return CommonResult.success(resultService.reviewResult(id, suggestion));
    }

    @ApiOperation("批量复核体检结果")
    @PostMapping("/batch/review")
    @HasPermission("exam:result:review")
    public CommonResult<List<ExamResultVO>> reviewResults(@RequestBody List<Long> ids) {
        return CommonResult.success(resultService.reviewResults(ids));
    }

    @ApiOperation("导出体检报告")
    @GetMapping("/export/{recordId}")
    @HasPermission("exam:result:export")
    public CommonResult<String> exportReport(@PathVariable Long recordId) {
        return CommonResult.success(resultService.exportReport(recordId));
    }
} 