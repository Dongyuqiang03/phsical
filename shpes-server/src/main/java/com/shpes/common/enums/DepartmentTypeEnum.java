package com.shpes.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门类型枚举
 */
@Getter
public enum DepartmentTypeEnum {
    MEDICAL(1, "医疗科室"),
    ACADEMIC(2, "教学院系"),
    OTHER(3, "其他部门");

    @EnumValue
    private final Integer value;
    
    @JsonValue
    private final String label;

    DepartmentTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据用户类型获取对应的部门类型列表
     * @param userType 用户类型枚举
     * @return 部门类型值列表
     */
    public static List<Integer> getDeptTypesByUserType(UserTypeEnum userType) {
        if (userType == null) {
            return Arrays.stream(values())
                    .map(DepartmentTypeEnum::getValue)
                    .collect(Collectors.toList());
        }

        switch (userType) {
            case MEDICAL_STAFF:
                return Arrays.asList(
                    MEDICAL.getValue(),
                    OTHER.getValue()
                );
            case TEACHER:
            case STUDENT:
                return Collections.singletonList(ACADEMIC.getValue());
            default:
                return Collections.emptyList();
        }
    }

    public static DepartmentTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (DepartmentTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}