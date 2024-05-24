package com.yu.ymind.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核状态枚举
 *
 * 该枚举类定义了审核状态的枚举值，包括通过、拒绝和待审核三种状态。
 */
public enum ReviewStatusEnum {

    REVIEWING("待审核", 0), // 待审核枚举值
    PASS("通过", 1), // 通过枚举值
    REJECT("拒绝", 2); // 拒绝枚举值

    /**
     * 审核状态描述文本
     */
    private final String text;

    /**
     * 审核状态
     */
    private final int value;

    /**
     * 枚举构造方法
     *
     * @param text 角色描述文本
     * @param value 角色值
     */
    ReviewStatusEnum(String text, int value) {
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
     * @param value 审核状态
     * @return 返回与审核状态对应的枚举对象，如果找不到则返回 null
     */
    public static ReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ReviewStatusEnum anEnum : ReviewStatusEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取审核状态值
     *
     * @return 返回枚举对象的角色值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取审核状态文本
     *
     * @return 返回枚举对象的角色描述文本
     */
    public String getText() {
        return text;
    }
}