package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检记录返回值对象
 */
@Data
@ApiModel(description = "体检记录返回值对象")
public class ExamRecordVO {

    @ApiModelProperty("记录 ID")
    private Long id;

    @ApiModelProperty("体检编号")
    private String examNo;
    
    @ApiModelProperty("用户ID")
    private Long userId;
    
    @ApiModelProperty("用户姓名")
    private String userName;
    
    @ApiModelProperty("预约ID")
    private Long appointmentId;
    
    @ApiModelProperty("预约编号")
    private String appointmentNo;
    
    @ApiModelProperty("体检套餐ID")
    private Long packageId;
    
    @ApiModelProperty("体检套餐名称")
    private String packageName;
    
    @ApiModelProperty("体检总结")
    private String summary;
    
    @ApiModelProperty("体检建议")
    private String suggestion;
    
    @ApiModelProperty("体检状态：0-未开始，1-进行中，2-已完成")
    private Integer status;
    
    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;
    
    @ApiModelProperty("总结医生ID")
    private Long doctorId;
    
    @ApiModelProperty("总结医生姓名")
    private String doctorName;
    
    @ApiModelProperty("异常项目数")
    private Integer abnormalCount;
    
    @ApiModelProperty("已检查项目数")
    private Integer checkedCount;
    
    @ApiModelProperty("总项目数")
    private Integer totalCount;
    
    @ApiModelProperty("体检结果列表")
    private List<ExamResultVO> results;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("用户性别")
    private Integer gender;

    @ApiModelProperty("用户年龄")
    private Integer age;

    @ApiModelProperty("联系电话")
    private String phone;
} 