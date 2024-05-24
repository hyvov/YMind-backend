package com.yu.ymind.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * APP应用类型枚举
 *
 * 该枚举类定义了APP应用类型的枚举值，包括得分类、测评类两种状态。
 */
public enum AppTypeEnum {

    SCORE("得分类", 0), // 得分类枚举值
    TEST("测评类", 1); // 测评类枚举值


    /**
     * APP应用状态描述文本
     */
    private final String text;

    /**
     * APP应用状态
     */
    private final int value;

    /**
     * 枚举构造方法
     *
     * @param text APP应用描述文本
     * @param value APP应用值
     */
    AppTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取所有枚举值的列表
     *
     * @return 返回包含所有枚举值的列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据value获取对应的枚举对象
     *
     * @param value APP应用状态
     * @return 返回与APP应用状态对应的枚举对象，如果找不到则返回 null
     */
    public static AppTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AppTypeEnum anEnum : AppTypeEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取APP应用状态值
     *
     * @return 返回枚举对象的APP应用值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取APP应用状态文本
     *
     * @return 返回枚举对象的APP应用描述文本
     */
    public String getText() {
        return text;
    }
}