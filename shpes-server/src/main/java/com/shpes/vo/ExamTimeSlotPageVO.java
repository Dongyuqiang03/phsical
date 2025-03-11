package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 体检时间段分页返回值对象
 */
@Data
@ApiModel(description = "体检时间段分页返回值对象")
public class ExamTimeSlotPageVO {
    
    @ApiModelProperty("总记录数")
    private Long total;
    
    @ApiModelProperty("当前页码")
    private Integer pageNum;
    
    @ApiModelProperty("每页记录数")
    private Integer pageSize;
    
    @ApiModelProperty("时间段列表")
    private List<ExamTimeSlotVO> records;
} 