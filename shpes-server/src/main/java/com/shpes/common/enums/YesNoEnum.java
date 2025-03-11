package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 是否枚举
 */
@Getter
public enum YesNoEnum {
    NO(0, "否"),
    YES(1, "是");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    YesNoEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
} 