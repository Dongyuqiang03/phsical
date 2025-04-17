package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 体检结果实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_result")
@ApiModel("体检结果")
public class ExamResult extends BaseEntity {

    @ApiModelProperty("体检记录ID")
    private Long recordId;

    @ApiModelProperty("体检项目ID")
    private Long itemId;

    @ApiModelProperty("检查结果值")
    private String value;

    @ApiModelProperty("是否异常：0-正常，1-异常")
    private Integer abnormal;

    @ApiModelProperty("异常分析")
    private String analysis;

    @ApiModelProperty("检查医生ID")
    private Long doctorId;

    @ApiModelProperty("复核医生ID")
    private Long reviewerId;

    @ApiModelProperty("状态：1-待复核，2-已复核")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
} 