package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检预约实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_appointment")
@ApiModel("体检预约")
public class ExamAppointment extends BaseEntity {

    @ApiModelProperty("预约编号")
    private String appointmentNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("套餐ID")
    private Long packageId;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("时间段ID")
    private Long timeSlotId;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("预约日期")
    private LocalDate appointmentDate;

    @ApiModelProperty("状态（0-待确认，1-待体检，2-进行中，3-已完成，4-已取消）")
    private Integer status;

    @ApiModelProperty("取消原因")
    private String cancelReason;

    @ApiModelProperty("取消时间")
    private LocalDateTime cancelTime;
}