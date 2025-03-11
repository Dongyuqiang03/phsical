package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 体检结果类型枚举
 */
@Getter
public enum ExamResultEnum {
    NORMAL(1, "正常"),
    ABNORMAL(2, "异常"),
    CRITICAL(3, "危急"),
    PENDING(4, "待复查");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    ExamResultEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
} 