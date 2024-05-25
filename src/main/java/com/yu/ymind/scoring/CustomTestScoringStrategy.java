package com.yu.ymind.scoring;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yu.ymind.model.dto.question.QuestionContentDTO;
import com.yu.ymind.model.entity.App;
import com.yu.ymind.model.entity.Question;
import com.yu.ymind.model.entity.ScoringResult;
import com.yu.ymind.model.entity.UserAnswer;
import com.yu.ymind.model.vo.QuestionVO;
import com.yu.ymind.service.AppService;
import com.yu.ymind.service.QuestionService;
import com.yu.ymind.service.ScoringResultService;
import com.yu.ymind.service.UserService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        // todo 根据id查询到题目和对应结果信息
        Long appId = app.getId();// 根据应用ID查询题目信息
        Question question = questionService.getOne(//查询数据库中的一个记录并返回结果
                Wrappers.lambdaQuery(Question.class)// 创建查询包装器，指定查询实体为Question
                        .eq(Question::getAppId, appId) // 添加查询条件，查询appId字段等于appId参数的记录
        );

        List<ScoringResult> scoringResultList = scoringResultService.list(//查询数据库中的多条记录并返回结果
                Wrappers.lambdaQuery(ScoringResult.class)// 创建查询包装器，指定查询实体为ScoringResult
                        .eq(ScoringResult::getAppId, appId)// 添加查询条件，查询appId字段等于appId参数的记录
        );


        //todo 统计用户每个选择对应的属性个数，如 I=10个，E=5个。

        // 初始化一个Map，用于存储每个选项的计数
        Map<String, Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);//将查询到的题目信息转化为脱敏对象
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();//从QuestionVO对象中获取题目内容，并将结果存储在questionContent列表中
        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历答案列表
            for (String answer : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的key匹配
                    if (option.getKey().equals(answer)) {
                        // 获取选项的result属性
                        String result = option.getResult();

                        // 如果result属性不在optionCount中，初始化为0
                        if (!optionCount.containsKey(result)) {
                            optionCount.put(result, 0);
                        }

                        // 在optionCount中增加计数
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }


        //todo 遍历每种评分结果，计算哪个结果的得分更高

        // 初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);

        // 遍历评分结果列表
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            // 计算当前评分结果的分数，[I, E] => [10, 5] => 15

            int score = resultProp.stream()//将 resultProp 列表转换为 IntStream。
                    .mapToInt(//使用 mapToInt 方法将 IntStream 中的元素转换为整数
                            prop -> optionCount.getOrDefault(prop, 0)//用于从optionCount哈希表中获取prop对应的值，如果prop不存在，则返回默认值 0。
                    )
                    .sum();//我们使用 sum 方法计算 IntStream 中所有元素的和,并将结果存储在 score 变量中。

            // 如果分数高于当前最高分数，更新最高分数和最高分数对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }

        //todo 构造返回值、填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
