package com.shpes.controller;

import com.shpes.common.api.CommonPage;
import com.shpes.common.api.CommonResult;
import com.shpes.common.constant.RoleConstants;
import com.shpes.dto.ExamTimeSlotDTO;
import com.shpes.dto.ExamTimeSlotBatchDTO;
import com.shpes.dto.ExamTimeSlotUpdateDTO;
import com.shpes.annotation.RequiresPermission;
import com.shpes.service.ExamPackageService;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.vo.ExamTimeSlotVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
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
    
    @Autowired
    private ExamPackageService examPackageService;

    @ApiOperation("获取时间段列表")
    @GetMapping("/list")
//    @RequiresPermission("exam:timeslot")
    public CommonResult<CommonPage<ExamTimeSlotVO>> getTimeSlotPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getTimeSlotPage(pageNum, pageSize, deptId, date));
    }

    @ApiOperation("获取时间段详情")
    @GetMapping("/{id}")
//    @RequiresPermission("exam:timeslot")
    public CommonResult<ExamTimeSlotVO> getTimeSlot(@PathVariable Long id) {
        return CommonResult.success(timeSlotService.getTimeSlot(id));
    }

    @ApiOperation("获取可预约时间段")
    @GetMapping("/available")
//    @RequiresPermission("exam:timeslot")
    public CommonResult<List<ExamTimeSlotVO>> getAvailableTimeSlots(
            @RequestParam Long deptId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return CommonResult.success(timeSlotService.getAvailableTimeSlots(deptId, date));
    }
    
    @ApiOperation("根据套餐ID获取可预约时间段")
    @GetMapping("/available/package")
//    @RequiresPermission("exam:appointment")
    public CommonResult<List<ExamTimeSlotVO>> getAvailableTimeSlotsForPackage(
            @RequestParam Long packageId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 不再查找科室ID，直接基于日期查询可用时间段
        // 后续可以增加对套餐特殊需求的处理，如某些套餐需要特定科室或特定设备
        
        // 查询指定日期的所有可用时间段
        List<ExamTimeSlotVO> availableTimeSlots = timeSlotService.getAvailableTimeSlotsByDate(date);
        
        return CommonResult.success(availableTimeSlots);
    }

    @ApiOperation("创建时间段")
    @PostMapping
    @RequiresPermission("exam:timeslot")
    public CommonResult<Void> createTimeSlot(@Valid @RequestBody ExamTimeSlotDTO timeSlotDTO) {
        timeSlotService.createTimeSlot(timeSlotDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("批量创建时间段")
    @PostMapping("/batch")
    @RequiresPermission("exam:timeslot")
    public CommonResult<Void> batchCreateTimeSlot(@Valid @RequestBody ExamTimeSlotBatchDTO batchDTO) {
        timeSlotService.batchCreateTimeSlot(batchDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("更新时间段")
    @PutMapping("/{id}")
    @RequiresPermission("exam:timeslot")
    public CommonResult<Void> updateTimeSlot(@PathVariable Long id, @Valid @RequestBody ExamTimeSlotUpdateDTO updateDTO) {
        // 设置ID
        updateDTO.setId(id);
        timeSlotService.updateTimeSlot(id, updateDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("删除时间段")
    @DeleteMapping("/{id}")
    @RequiresPermission("exam:timeslot")
    public CommonResult<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return CommonResult.success(null);
    }
} 
