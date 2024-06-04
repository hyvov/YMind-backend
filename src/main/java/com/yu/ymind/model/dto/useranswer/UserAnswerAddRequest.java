package com.yu.ymind.model.dto.useranswer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建用户答案请求
 *
 *
 */
@Data
public class UserAnswerAddRequest implements Serializable {
    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

    /**
     * 用户提交答案时补充id(用于保证幂等性)
     */
    private long id;

    private static final long serialVersionUID = 1L;

}