package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 体检时间段批量设置数据传输对象
 */
@Data
@ApiModel("体检时间段批量设置DTO")
public class ExamTimeSlotBatchDTO {

    @ApiModelProperty(value = "开始日期", required = true)
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期", required = true)
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @ApiModelProperty(value = "科室ID", required = true)
    @NotNull(message = "科室ID不能为空")
    private Long deptId;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    @ApiModelProperty(value = "容量", required = true)
    @NotNull(message = "容量不能为空")
    private Integer capacity;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status = 1;

    @ApiModelProperty("备注")
    private String remark;
    
    @ApiModelProperty("禁用的日期列表（如周末或特定日期）")
    private List<LocalDate> excludeDates;
} 