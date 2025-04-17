package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 体检结果更新DTO
 */
@Data
@ApiModel("体检结果更新请求")
public class ExamResultUpdateDTO {

    @ApiModelProperty("体检记录ID")
    private Long id;

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("体检项目结果列表")
    private List<ExamResultItemUpdateDTO> items;

    @ApiModelProperty("体检结论")
    private String conclusion;

    @ApiModelProperty("体检结果")
    private String mainFindings;

    @ApiModelProperty("医生建议")
    private String suggestion;

    /**
     * 体检结果项DTO
     */
    @Data
    @ApiModel("体检结果项")
    public static class ExamResultItemUpdateDTO {
        
        @ApiModelProperty("体检结果主键 ID")
        private Long id;
        
        @ApiModelProperty("检查结果")
        private String result;
        
        @ApiModelProperty("状态：NORMAL-正常，ABNORMAL-异常")
        private String status;
        
        @ApiModelProperty("异常分析")
        private String analysis;
    }
}