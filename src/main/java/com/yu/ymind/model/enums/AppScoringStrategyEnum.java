package com.yu.ymind.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * APP评分策略枚举
 *
 * 该枚举类定义了AI评分策略的枚举值，包括自定义，AI两种状态。
 */
public enum AppScoringStrategyEnum {

    CUSTOM("自定义", 0), // 自定义枚举值
    AI("AI", 1); // AI枚举值

    /**
     * APP评分策略描述文本
     */
    private final String text;

    /**
     * APP评分策略
     */
    private final int value;

    /**
     * 枚举构造方法
     *
     * @param text APP评分描述文本
     * @param value APP评分值
     */
    AppScoringStrategyEnum(String text, int value) {
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
     * @param value AI评分策略
     * @return 返回与AI评分策略对应的枚举对象，如果找不到则返回 null
     */
    public static AppScoringStrategyEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AppScoringStrategyEnum anEnum : AppScoringStrategyEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取AI评分策略值
     *
     * @return 返回枚举对象的角色值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取AI评分策略文本
     *
     * @return 返回枚举对象的角色描述文本
     */
    public String getText() {
        return text;
    }
}