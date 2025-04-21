package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("体检记录详情VO")
public class ExamRecordDetailVO {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("体检编号")
    private String examNo;

    @ApiModelProperty("预约编号")
    private String appointmentNo;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("性别")
    private Integer gender;


    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("体检日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime examDate;

    @ApiModelProperty("主要发现")
    private String mainFindings;

    @ApiModelProperty("体检结论")
    private String conclusion;

    @ApiModelProperty("医生建议")
    private String suggestion;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("体检项目结果列表")
    private List<ExamResultDetailVO> results;  // 将 ExamItemResultVO 改为 ExamResultDetailVO
}