package com.yu.ymind.model.dto.statistic;

import lombok.Data;

/**
 * AppAnswerCountDTO 类，用于封装应用回答统计数据。
 */
@Data
public class AppAnswerCountDTO {
    private long appId;
    /**
     * 用户提交答案数
     */
    private long answerCount;


}
