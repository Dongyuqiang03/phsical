package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.entity.ExamAppointment;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamAppointmentService;
import com.shpes.utils.SecurityUtils;
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
    @RequiresPermission("exam:appointment")
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
    @RequiresPermission("exam:appointment")
    public CommonResult<AppointmentVO> getAppointment(@PathVariable Long id) {
        return CommonResult.success(appointmentService.getAppointment(id));
    }

    @ApiOperation("创建预约")
    @PostMapping
    @RequiresPermission("exam:appointment")
    public CommonResult<AppointmentVO> createAppointment(@Valid @RequestBody ExamAppointment appointment) {
        // 设置当前登录用户ID
        appointment.setUserId(SecurityUtils.getCurrentUserId());
        
        // 创建预约并返回预约信息
        AppointmentVO vo = appointmentService.createAppointmentAndReturn(appointment);
        
        return CommonResult.success(vo);
    }

    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    @RequiresPermission("exam:appointment")
    public CommonResult<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam String reason) {
        appointmentService.cancelAppointment(id, reason);
        return CommonResult.success(null);
    }

    @ApiOperation("更新预约时间")
    @PutMapping("/{id}/time")
    @RequiresPermission("exam:appointment")
    public CommonResult<Void> updateAppointmentTime(
            @PathVariable Long id,
            @RequestParam Long timeSlotId) {
        appointmentService.updateAppointmentTime(id, timeSlotId);
        return CommonResult.success(null);
    }

    @ApiOperation("完成预约")
    @PutMapping("/{id}/complete")
    @RequiresPermission("exam:appointment")
    public CommonResult<Void> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return CommonResult.success(null);
    }

    @ApiOperation("开始体检（签到）")
    @PutMapping("/{id}/start")
    @RequiresPermission("exam:appointment")
    public CommonResult<Void> startAppointment(@PathVariable Long id) {
        appointmentService.startAppointment(id);
        return CommonResult.success(null);
    }

    @ApiOperation("获取当前用户的预约列表")
    @GetMapping("/user")
    @RequiresPermission("exam:appointment")
    public CommonResult<CommonPage<AppointmentVO>> getUserAppointments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        return CommonResult.success(appointmentService.getUserAppointments(userId, pageNum, pageSize, startDate, endDate, status));
    }
} 
