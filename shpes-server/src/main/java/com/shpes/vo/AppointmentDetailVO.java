package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约详情返回值对象
 */
@Data
@ApiModel(description = "预约详情返回值对象")
public class AppointmentDetailVO {
    
    @ApiModelProperty("预约ID")
    private Long id;
    
    @ApiModelProperty("预约编号")
    private String appointmentNo;
    
    @ApiModelProperty("预约人")
    private String userName;
    
    @ApiModelProperty("联系电话")
    private String phone;
    
    @ApiModelProperty("预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    @ApiModelProperty("体检科室")
    private String deptName;
    
    @ApiModelProperty("预约状态：0-待确认, 1-待体检，2-进行中，3-已完成，4-已取消")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("体检套餐信息")
    private PackageInfo packageInfo;
    
    @ApiModelProperty("体检项目列表")
    private List<ExamItemInfo> examItems;
    
    @Data
    public static class PackageInfo {
        @ApiModelProperty("套餐名称")
        private String name;
        
        @ApiModelProperty("套餐说明")
        private String description;
    }
    
    @Data
    public static class ExamItemInfo {
        @ApiModelProperty("项目名称")
        private String name;
        
        @ApiModelProperty("检查科室")
        private String deptName;
        
        @ApiModelProperty("项目说明")
        private String description;
        
        @ApiModelProperty("注意事项")
        private String notice;
    }
}