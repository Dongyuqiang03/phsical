package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.vo.ExamTimeSlotVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 体检时间段管理控制器
 */
@Api(tags = "体检时间段管理")
@RestController
@RequestMapping("/exam/timeslots")
public class ExamTimeSlotController {

    @Autowired
    private ExamTimeSlotService timeSlotService;

    @ApiOperation("获取时间段列表")
    @GetMapping("/list")
    @RequiresPermission("")
    public CommonResult<CommonPage<ExamTimeSlotVO>> getTimeSlotPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getTimeSlotPage(pageNum, pageSize, departmentId, date));
    }

    @ApiOperation("获取时间段详情")
    @GetMapping("/{id}")
    @RequiresPermission("")
    public CommonResult<ExamTimeSlotVO> getTimeSlot(@PathVariable Long id) {
        return CommonResult.success(timeSlotService.getTimeSlot(id));
    }

    @ApiOperation("获取可预约时间段")
    @GetMapping("/available")
    @RequiresPermission("")
    public CommonResult<List<ExamTimeSlotVO>> getAvailableTimeSlots(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getAvailableTimeSlots(departmentId, date));
    }

    @ApiOperation("创建时间段")
    @PostMapping
    @RequiresPermission("")
    public CommonResult<Void> createTimeSlot(@Valid @RequestBody ExamTimeSlot timeSlot) {
        timeSlotService.createTimeSlot(timeSlot);
        return CommonResult.success(null);
    }

    @ApiOperation("更新时间段")
    @PutMapping("/{id}")
    @RequiresPermission("")
    public CommonResult<Void> updateTimeSlot(@PathVariable Long id, @Valid @RequestBody ExamTimeSlot timeSlot) {
        timeSlot.setId(id);
        timeSlotService.updateTimeSlot(timeSlot);
        return CommonResult.success(null);
    }

    @ApiOperation("删除时间段")
    @DeleteMapping("/{id}")
    @RequiresPermission("")
    public CommonResult<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新时间段状态")
    @PutMapping("/{id}/status")
    @RequiresPermission("")
    public CommonResult<Void> updateTimeSlotStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        timeSlotService.updateStatus(id, status);
        return CommonResult.success(null);
    }
} 
