package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.entity.ExamRecord;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.ExamRecordService;
import com.shpes.vo.ExamRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 体检记录管理控制器
 */
@Api(tags = "体检记录管理")
@RestController
@RequestMapping("/api/records")
public class ExamRecordController {

    @Autowired
    private ExamRecordService recordService;

    @ApiOperation("分页查询体检记录")
    @GetMapping
    @HasPermission("exam:record:list")
    public CommonResult<CommonPage<ExamRecordVO>> getRecordPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("体检状态：0-未开始，1-进行中，2-已完成") @RequestParam(required = false) Integer status,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResult.success(recordService.getRecordPage(pageNum, pageSize, userId, status, startDate, endDate));
    }

    @ApiOperation("获取体检记录详情")
    @GetMapping("/{id}")
    @HasPermission("exam:record:list")
    public CommonResult<ExamRecordVO> getRecordById(@PathVariable Long id) {
        return CommonResult.success(recordService.getRecordById(id));
    }

    @ApiOperation("获取用户体检记录列表")
    @GetMapping("/user/{userId}")
    @HasPermission("exam:record:list")
    public CommonResult<List<ExamRecordVO>> getUserRecords(@PathVariable Long userId) {
        return CommonResult.success(recordService.getUserRecords(userId));
    }

    @ApiOperation("创建体检记录")
    @PostMapping
    @HasPermission("exam:record:create")
    public CommonResult<ExamRecordVO> createRecord(@Valid @RequestBody ExamRecord record) {
        return CommonResult.success(recordService.createRecord(record));
    }

    @ApiOperation("更新体检记录")
    @PutMapping("/{id}")
    @HasPermission("exam:record:update")
    public CommonResult<ExamRecordVO> updateRecord(@PathVariable Long id, @Valid @RequestBody ExamRecord record) {
        record.setId(id);
        return CommonResult.success(recordService.updateRecord(record));
    }

    @ApiOperation("删除体检记录")
    @DeleteMapping("/{id}")
    @HasPermission("exam:record:delete")
    public CommonResult<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新体检状态")
    @PutMapping("/{id}/status")
    @HasPermission("exam:record:update")
    public CommonResult<ExamRecordVO> updateRecordStatus(
            @PathVariable Long id,
            @ApiParam("体检状态：0-未开始，1-进行中，2-已完成") @RequestParam Integer status) {
        return CommonResult.success(recordService.updateStatus(id, status));
    }

    @ApiOperation("完成体检记录")
    @PutMapping("/{id}/complete")
    @HasPermission("exam:record:update")
    public CommonResult<ExamRecordVO> completeRecord(
            @PathVariable Long id,
            @RequestParam String summary,
            @RequestParam(required = false) String suggestion) {
        return CommonResult.success(recordService.completeRecord(id, summary, suggestion));
    }

    @ApiOperation("获取今日体检统计")
    @GetMapping("/stats/today")
    @HasPermission("exam:record:stats")
    public CommonResult<Object> getTodayStats() {
        return CommonResult.success(recordService.getTodayStats());
    }

    @ApiOperation("获取体检完成率统计")
    @GetMapping("/stats/completion")
    @HasPermission("exam:record:stats")
    public CommonResult<Object> getCompletionStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResult.success(recordService.getCompletionStats(startDate, endDate));
    }
} 