package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 体检时间段更新数据传输对象
 * 仅用于更新操作，放宽字段验证
 */
@Data
@ApiModel("体检时间段更新DTO")
public class ExamTimeSlotUpdateDTO {

    @ApiModelProperty("ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @ApiModelProperty("科室ID")
    private Long deptId;

    @ApiModelProperty("日期")
    private LocalDate date;

    @ApiModelProperty("开始时间")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    private LocalTime endTime;

    @ApiModelProperty("容量")
    private Integer capacity;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
