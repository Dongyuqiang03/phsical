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

    @ApiModelProperty("检查结果值 - 对应实体类的value字段")
    private String value;

    @ApiModelProperty("参考值")
    private String reference;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("是否异常：0-正常，1-异常 - 对应实体类的abnormal字段")
    private Integer abnormal;

    @ApiModelProperty("医生建议 - 对应实体类的suggestion字段")
    private String suggestion;

    @ApiModelProperty("检查医生ID")
    private Long doctorId;
    
    @ApiModelProperty("复核医生ID")
    private Long reviewerId;
    
    @ApiModelProperty("医生姓名")
    private String doctorName;
    
    @ApiModelProperty("状态：1-待复核，2-已复核")
    private Integer status;
    
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}