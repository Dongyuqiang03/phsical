package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum {
    MEDICAL_STAFF(1, "医护人员"),
    TEACHER(2, "教职工"),
    STUDENT(3, "学生");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    UserTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static UserTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}