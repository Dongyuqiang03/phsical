package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 体检结果VO
 */
@Data
@ApiModel(description = "体检结果VO")
public class ExamResultVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("体检记录ID")
    private Long recordId;

    @ApiModelProperty("体检项目ID")
    private Long itemId;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("检查结果值")
    private String resultValue;

    @ApiModelProperty("参考值")
    private String referenceValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("是否异常（0-正常，1-异常）")
    private Integer isAbnormal;

    @ApiModelProperty("医生建议")
    private String doctorAdvice;

    @ApiModelProperty("医生ID")
    private Long doctorId;

    @ApiModelProperty("医生姓名")
    private String doctorName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 