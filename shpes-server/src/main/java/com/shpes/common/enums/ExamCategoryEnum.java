package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 体检项目分类枚举
 */
@Getter
public enum ExamCategoryEnum {
    GENERAL(1, "一般检查"),
    INTERNAL(2, "内科"),
    SURGERY(3, "外科"),
    OPHTHALMOLOGY(4, "眼科"),
    ENT(5, "耳鼻喉科"),
    DENTAL(6, "口腔科"),
    LABORATORY(7, "实验室检查"),
    IMAGING(8, "医学影像"),
    OTHER(9, "其他");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    ExamCategoryEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static ExamCategoryEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ExamCategoryEnum category : values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }
        return null;
    }
}