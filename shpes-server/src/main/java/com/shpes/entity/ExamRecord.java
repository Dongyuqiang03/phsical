package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 体检记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_record")
@ApiModel("体检记录")
public class ExamRecord extends BaseEntity {

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("体检编号")
    private String examNo;

    @ApiModelProperty("状态：1-进行中，2-已完成")
    private Integer status;

    @ApiModelProperty("体检总结")
    private String conclusion;

    @ApiModelProperty("医生建议")
    private String suggestion;

    @ApiModelProperty("总检医生ID")
    private Long doctorId;

    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;
} 