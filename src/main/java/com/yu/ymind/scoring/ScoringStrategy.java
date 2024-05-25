package com.yu.ymind.scoring;

import com.yu.ymind.model.entity.App;
import com.yu.ymind.model.entity.UserAnswer;

import java.util.List;

// 计算器接口
public interface ScoringStrategy {
    /**
     * 执行评分
     * @param choice
     * @param app
     * @return
     * @throws Exception
     */
    UserAnswer doScore(List<String> choice, App app) throws Exception;
}


