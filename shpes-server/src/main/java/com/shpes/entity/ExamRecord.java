package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 体检记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_record")
@ApiModel("体检记录")
public class ExamRecord extends BaseEntity {

    @ApiModelProperty("体检编号")
    private String examNo;

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("套餐ID")
    private Long packageId;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("医生ID")
    private Long doctorId;

    @ApiModelProperty("医生姓名")
    private String doctorName;

    @ApiModelProperty("体检日期")
    private LocalDateTime examDate;

    @ApiModelProperty("体检结论")
    private String conclusion;

    @ApiModelProperty("医生建议")
    private String suggestion;

    @ApiModelProperty("状态（0-待体检，1-进行中(未录入结果)，2-进行中(已录入结果)，3-已完成）")
    private Integer status;

    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;
} 