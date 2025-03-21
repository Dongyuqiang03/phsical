package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.ExamAppointment;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamAppointmentService;
import com.shpes.vo.AppointmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * 体检预约管理控制器
 */
@Api(tags = "体检预约管理")
@RestController
@RequestMapping("/exam/appointments")
public class ExamAppointmentController {

    @Autowired
    private ExamAppointmentService appointmentService;

    @ApiOperation("获取预约列表")
    @GetMapping("/list")
    @RequiresPermission("")
    public CommonResult<CommonPage<AppointmentVO>> getAppointmentPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer status) {
        return CommonResult.success(appointmentService.getAppointmentPage(pageNum, pageSize, userId, startDate, endDate, status));
    }

    @ApiOperation("获取预约详情")
    @GetMapping("/{id}")
    @RequiresPermission("")
    public CommonResult<AppointmentVO> getAppointment(@PathVariable Long id) {
        return CommonResult.success(appointmentService.getAppointment(id));
    }

    @ApiOperation("创建预约")
    @PostMapping
    @RequiresPermission("")
    public CommonResult<Void> createAppointment(@Valid @RequestBody ExamAppointment appointment) {
        appointmentService.createAppointment(appointment);
        return CommonResult.success(null);
    }

    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    @RequiresPermission("")
    public CommonResult<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam String reason) {
        appointmentService.cancelAppointment(id, reason);
        return CommonResult.success(null);
    }

    @ApiOperation("更新预约时间")
    @PutMapping("/{id}/time")
    @RequiresPermission("")
    public CommonResult<Void> updateAppointmentTime(
            @PathVariable Long id,
            @RequestParam Long timeSlotId) {
        appointmentService.updateAppointmentTime(id, timeSlotId);
        return CommonResult.success(null);
    }

    @ApiOperation("完成预约")
    @PutMapping("/{id}/complete")
    @RequiresPermission("")
    public CommonResult<Void> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return CommonResult.success(null);
    }
} 
