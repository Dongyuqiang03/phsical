package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum GenderEnum {
    MALE(1, "男"),
    FEMALE(2, "女");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    GenderEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
} 