package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 体检时间段返回值对象
 */
@Data
@ApiModel(description = "体检时间段返回值对象")
public class ExamTimeSlotVO {
    
    @ApiModelProperty("时间段ID")
    private Long id;
    
    @ApiModelProperty("科室ID")
    private Long departmentId;
    
    @ApiModelProperty("科室名称")
    private String departmentName;
    
    @ApiModelProperty("日期")
    private LocalDate date;
    
    @ApiModelProperty("开始时间")
    private LocalTime startTime;
    
    @ApiModelProperty("结束时间")
    private LocalTime endTime;
    
    @ApiModelProperty("容量")
    private Integer capacity;
    
    @ApiModelProperty("已预约数量")
    private Integer appointmentCount;
    
    @ApiModelProperty("剩余可预约数量")
    private Integer remainingCapacity;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 