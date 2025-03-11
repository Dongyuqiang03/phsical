package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 状态枚举
 */
@Getter
public enum StatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
} 