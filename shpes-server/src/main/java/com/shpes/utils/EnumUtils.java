package com.shpes.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举工具类
 */
public class EnumUtils {

    /**
     * 获取枚举的所有值
     */
    public static <T extends Enum<T>> List<T> getValues(Class<T> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }

    /**
     * 根据value获取枚举
     */
    public static <T extends Enum<T>> T getEnumByValue(Class<T> enumClass, Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> {
                    try {
                        return value.equals(e.getClass().getMethod("getValue").invoke(e));
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取枚举的所有标签
     */
    public static <T extends Enum<T>> List<String> getLabels(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> {
                    try {
                        return (String) e.getClass().getMethod("getLabel").invoke(e);
                    } catch (Exception ex) {
                        return "";
                    }
                })
                .collect(Collectors.toList());
    }
} 