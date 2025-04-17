package com.shpes.vo;

import com.shpes.entity.ExamResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamResultDetailVO extends ExamResult {
    private String itemName;
    private String itemCode;
    private String referenceValue;
    private String doctorName;
    private String reviewerName;
}