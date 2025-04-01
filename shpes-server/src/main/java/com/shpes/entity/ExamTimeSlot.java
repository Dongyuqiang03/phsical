package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 体检时间段实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_time_slot")
@ApiModel("体检时间段")
public class ExamTimeSlot extends BaseEntity {

    @ApiModelProperty("科室ID")
    private Long deptId;

    @ApiModelProperty("日期")
    private LocalDate date;

    @ApiModelProperty("开始时间")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    private LocalTime endTime;

    @ApiModelProperty("容量")
    private Integer capacity;

    @ApiModelProperty("已预约数量")
    private Integer bookedCount;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}