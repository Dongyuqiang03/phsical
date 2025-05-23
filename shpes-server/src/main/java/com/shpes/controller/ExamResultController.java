package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.dto.ExamResultUpdateDTO;
import com.shpes.entity.ExamRecord;
import com.shpes.entity.ExamResult;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamRecordService;
import com.shpes.service.ExamResultService;
import com.shpes.vo.ExamRecordVO;
import com.shpes.vo.ExamResultVO;
import com.shpes.dto.ExamResultBatchDTO;
import com.shpes.dto.ExamResultBatchDTO.ExamResultItemDTO;
import com.shpes.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * 体检结果管理控制器
 */
@Api(tags = "体检结果管理")
@RestController
@RequestMapping("/exam/results")
public class ExamResultController {

    @Autowired
    private ExamResultService resultService;

    @ApiOperation("获取体检结果列表")
    @GetMapping("/record/{recordId}")
    public CommonResult<List<ExamResultVO>> getResultsByRecordId(@PathVariable Long recordId) {
        return CommonResult.success(resultService.getResultsByRecordId(recordId));
    }

    @ApiOperation("分页查询体检结果")
    @GetMapping("/list")
    public CommonResult<CommonPage<ExamResultVO>> getResultPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("体检记录ID") @RequestParam(required = false) Long recordId,
            @ApiParam("体检项目ID") @RequestParam(required = false) Long itemId,
            @ApiParam("结果类型：0-正常，1-异常") @RequestParam(required = false) Integer resultType,
            @ApiParam("复核状态：0-待复核，1-已复核") @RequestParam(required = false) Integer reviewStatus) {
        return CommonResult.success(resultService.getResultPage(pageNum, pageSize, recordId, itemId, resultType, reviewStatus));
    }

    @ApiOperation("获取体检结果详情")
    @GetMapping("/{id}")
//    @RequiresPermission("exam:result")
    public CommonResult<ExamResultVO> getResult(@PathVariable Long id) {
        return CommonResult.success(resultService.getResultsByRecordId(id).get(0));
    }

    @ApiOperation("录入体检结果")
    @PostMapping
    @RequiresPermission("exam:result")
    public CommonResult<ExamResultVO> createResult(@Valid @RequestBody ExamResult result) {
        return CommonResult.success(resultService.createResult(result));
    }

    @ApiOperation("批量录入体检结果")
    @PostMapping("/batch")
    @RequiresPermission("exam:result")
    public CommonResult<List<ExamResultVO>> createResults(@Valid @RequestBody ExamResultBatchDTO batchDTO) {
        // 调用服务层方法处理业务逻辑
        return CommonResult.success(resultService.createResultsFromBatchDTO(batchDTO));
    }

    @ApiOperation("更新体检结果")
    @PutMapping("/{id}")
    @RequiresPermission("exam:result")
    public CommonResult<ExamResultVO> updateResult(@PathVariable Long id, @Valid @RequestBody ExamResult result) {
        result.setId(id);
        return CommonResult.success(resultService.updateResult(result));
    }

    @ApiOperation("批量更新体检结果")
    @PostMapping("/batch/update")
    @RequiresPermission("exam:result")
    public CommonResult<List<ExamResultVO>> updateResults(@Valid @RequestBody ExamResultUpdateDTO batchDTO) {
        return CommonResult.success(resultService.updateResultsFromBatchDTO(batchDTO));
    }

    @ApiOperation("删除体检结果")
    @DeleteMapping("/{id}")
    @RequiresPermission("exam:result")
    public CommonResult<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return CommonResult.success(null);
    }

    @ApiOperation("复核体检结果")
    @PutMapping("/{id}/review")
    @RequiresPermission("exam:result")
    public CommonResult<ExamResultVO> reviewResult(
            @PathVariable Long id,
            @RequestParam String suggestion) {
        return CommonResult.success(resultService.reviewResult(id, suggestion));
    }

    @ApiOperation("批量复核体检结果")
    @PostMapping("/batch/review")
    @RequiresPermission("exam:result")
    public CommonResult<List<ExamResultVO>> reviewResults(@RequestBody List<Long> ids) {
        return CommonResult.success(resultService.reviewResults(ids));
    }

    @ApiOperation("导出体检报告")
    @GetMapping("/export/{recordId}")
    public CommonResult<String> exportReport(@PathVariable Long recordId) {
        return CommonResult.success(resultService.exportReport(recordId));
    }

    @ApiOperation("获取异常结果列表")
    @GetMapping("/abnormal")
    public CommonResult<List<ExamResultVO>> getAbnormalResults(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long recordId) {
        return CommonResult.success(resultService.getResultPage(1, 100, recordId, null, 1, null).getRecords());
    }

    @ApiOperation("获取当前用户的体检结果")
    @GetMapping("/user/current")
    public CommonResult<CommonPage<ExamResultVO>> getCurrentUserResults(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("体检记录ID") @RequestParam(required = false) Long recordId,
            @ApiParam("开始日期") @RequestParam(required = false) String beginDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate,
            @ApiParam("项目名称") @RequestParam(required = false) String itemName,
            @ApiParam("结果类型：0-正常，1-异常") @RequestParam(required = false) Integer resultType) {
        
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 调用Service查询当前用户的体检结果
        return CommonResult.success(resultService.getCurrentUserResults(userId, pageNum, pageSize, 
            recordId, beginDate, endDate, itemName, resultType));
    }

}
