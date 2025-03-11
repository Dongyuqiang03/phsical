package com.shpes.controller;

import com.shpes.common.api.CommonResult;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.security.annotation.HasPermission;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.vo.ExamTimeSlotVO;
import com.shpes.vo.ExamTimeSlotPageVO;
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
@RequestMapping("/api/timeslots")
public class ExamTimeSlotController {

    @Autowired
    private ExamTimeSlotService timeSlotService;

    @ApiOperation("获取时间段列表")
    @GetMapping
    @HasPermission("exam:timeslot:list")
    public CommonResult<ExamTimeSlotPageVO> getTimeSlotPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getTimeSlotPage(pageNum, pageSize, departmentId, date));
    }

    @ApiOperation("获取可预约时间段")
    @GetMapping("/available")
    public CommonResult<List<ExamTimeSlotVO>> getAvailableTimeSlots(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getAvailableTimeSlots(departmentId, date));
    }

    @ApiOperation("创建时间段")
    @PostMapping
    @HasPermission("exam:timeslot:create")
    public CommonResult<ExamTimeSlotVO> createTimeSlot(@Valid @RequestBody ExamTimeSlot timeSlot) {
        return CommonResult.success(timeSlotService.createTimeSlot(timeSlot));
    }

    @ApiOperation("更新时间段")
    @PutMapping("/{id}")
    @HasPermission("exam:timeslot:update")
    public CommonResult<ExamTimeSlotVO> updateTimeSlot(@PathVariable Long id, @Valid @RequestBody ExamTimeSlot timeSlot) {
        timeSlot.setId(id);
        return CommonResult.success(timeSlotService.updateTimeSlot(timeSlot));
    }

    @ApiOperation("删除时间段")
    @DeleteMapping("/{id}")
    @HasPermission("exam:timeslot:delete")
    public CommonResult<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return CommonResult.success(null);
    }

    @ApiOperation("更新时间段状态")
    @PutMapping("/{id}/status")
    @HasPermission("exam:timeslot:update")
    public CommonResult<ExamTimeSlotVO> updateTimeSlotStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return CommonResult.success(timeSlotService.updateStatus(id, status));
    }
} 