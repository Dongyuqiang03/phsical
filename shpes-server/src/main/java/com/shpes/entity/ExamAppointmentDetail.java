package com.shpes.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 体检预约详情实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("体检预约详情")
public class ExamAppointmentDetail extends ExamAppointment {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户真实姓名")
    private String userName;

    @ApiModelProperty("时间段开始时间")
    private LocalTime startTime;

    @ApiModelProperty("时间段结束时间")
    private LocalTime endTime;
}