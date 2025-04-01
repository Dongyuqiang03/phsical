package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约返回值对象
 */
@Data
@ApiModel(description = "预约返回值对象")
public class AppointmentVO {
    
    @ApiModelProperty("预约ID")
    private Long id;
    
    @ApiModelProperty("预约编号")
    private String appointmentNo;
    
    @ApiModelProperty("用户ID")
    private Long userId;
    
    @ApiModelProperty("用户姓名")
    private String userName;
    
    @ApiModelProperty("套餐ID")
    private Long packageId;
    
    @ApiModelProperty("套餐名称")
    private String packageName;
    
    @ApiModelProperty("时间段ID")
    private Long timeSlotId;
    
    @ApiModelProperty("科室ID")
    private Long deptId;
    
    @ApiModelProperty("科室名称")
    private String deptName;
    
    @ApiModelProperty("预约日期")
    private LocalDate appointmentDate;
    
    @ApiModelProperty("开始时间")
    private LocalTime startTime;
    
    @ApiModelProperty("结束时间")
    private LocalTime endTime;
    
    @ApiModelProperty("预约时间")
    private LocalDateTime appointmentTime;
    
    @ApiModelProperty("预约状态：0-待确认, 1-待体检，2-进行中，3-已完成，4-已取消")
    private Integer status;
    
    @ApiModelProperty("取消原因")
    private String cancelReason;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 