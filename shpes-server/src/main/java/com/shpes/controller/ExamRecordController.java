package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.ExamRecord;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamRecordService;
import com.shpes.vo.ExamRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 体检记录管理控制器
 */
@Api(tags = "体检记录管理")
@RestController
@RequestMapping("/exam/records")
public class ExamRecordController {

    @Autowired
    private ExamRecordService recordService;

    @ApiOperation("获取记录列表")
    @GetMapping("/list")
    @RequiresPermission("exam:record")
    public CommonResult<CommonPage<ExamRecordVO>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResult.success(recordService.getRecordPage(pageNum, pageSize, userId, status, startDate, endDate));
    }

    @ApiOperation("获取记录详情")
    @GetMapping("/{id}")
    @RequiresPermission("exam:record")
    public CommonResult<ExamRecordVO> getRecord(@PathVariable Long id) {
        return CommonResult.success(recordService.getRecordById(id));
    }

    @ApiOperation("获取用户体检记录列表")
    @GetMapping("/user/{userId}")
    @RequiresPermission("exam:record")
    public CommonResult<List<ExamRecordVO>> getUserRecords(@PathVariable Long userId) {
        return CommonResult.success(recordService.getUserRecords(userId));
    }

    @ApiOperation("创建记录")
    @PostMapping
    @RequiresPermission("exam:record")
    public CommonResult<ExamRecordVO> createRecord(@Valid @RequestBody ExamRecord record) {
        return CommonResult.success(recordService.createRecord(record));
    }

    @ApiOperation("更新记录")
    @PutMapping("/{id}")
    @RequiresPermission("exam:record")
    public CommonResult<ExamRecordVO> updateRecord(@PathVariable Long id, @Valid @RequestBody ExamRecord record) {
        record.setId(id);
        return CommonResult.success(recordService.updateRecord(record));
    }

    @ApiOperation("删除记录")
    @DeleteMapping("/{id}")
    @RequiresPermission("exam:record")
    public CommonResult<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新记录状态")
    @PutMapping("/{id}/status")
    @RequiresPermission("exam:record")
    public CommonResult<ExamRecordVO> updateRecordStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return CommonResult.success(recordService.updateStatus(id, status));
    }

    @ApiOperation("完成体检记录")
    @PutMapping("/{id}/complete")
    @RequiresPermission("exam:record")
    public CommonResult<ExamRecordVO> completeRecord(
            @PathVariable Long id,
            @RequestParam String conclusion,
            @RequestParam(required = false) String suggestion) {
        return CommonResult.success(recordService.completeRecord(id, conclusion, suggestion));
    }

    @ApiOperation("获取今日体检统计")
    @GetMapping("/stats/today")
    @RequiresPermission("exam:record")
    public CommonResult<Object> getTodayStats() {
        return CommonResult.success(recordService.getTodayStats());
    }

    @ApiOperation("获取体检完成率统计")
    @GetMapping("/stats/completion")
    @RequiresPermission("exam:record")
    public CommonResult<List<Map<String, Object>>> getCompletionStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResult.success(recordService.getCompletionStats(startDate, endDate));
    }
} 
