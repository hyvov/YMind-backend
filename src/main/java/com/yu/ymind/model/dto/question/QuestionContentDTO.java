package com.yu.ymind.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {


    /**
     * 题目选项列表
     */
    private List<Option> options;

    /**
     * 题目标题
     */
    private String title;


    @Data//自动生成所有属性的 getter、setter 方法，以及 equals、hashCode 和 toString 方法。
    @NoArgsConstructor//自动生成无参构造方法。
    @AllArgsConstructor//自动生成包含所有属性的构造方法。
    public static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}