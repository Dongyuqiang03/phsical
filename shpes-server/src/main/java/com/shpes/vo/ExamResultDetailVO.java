package com.shpes.vo;

import com.shpes.entity.ExamResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamResultDetailVO extends ExamResult {
    @ApiModelProperty("体检项目名称")
    private String itemName;
    @ApiModelProperty("体检项目编码")
    private String itemCode;
    @ApiModelProperty("体检项目参考范围")
    private String referenceValue;
    private String doctorName;
    private String reviewerName;
}