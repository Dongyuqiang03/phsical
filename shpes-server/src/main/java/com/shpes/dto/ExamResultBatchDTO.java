package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 体检结果批量保存DTO
 */
@Data
@ApiModel("体检结果批量保存请求")
public class ExamResultBatchDTO {

    @ApiModelProperty("体检记录ID")
    private Long id;

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("体检项目结果列表")
    private List<ExamResultItemDTO> items;

    /**
     * 体检结果项DTO
     */
    @Data
    @ApiModel("体检结果项")
    public static class ExamResultItemDTO {
        
        @ApiModelProperty("体检项目ID")
        private Long itemId;
        
        @ApiModelProperty("体检项目名称")
        private String itemName;
        
        @ApiModelProperty("检查结果")
        private String result;
        
        @ApiModelProperty("状态：NORMAL-正常，ABNORMAL-异常")
        private String status;
        
        @ApiModelProperty("异常分析")
        private String analysis;
    }
} 