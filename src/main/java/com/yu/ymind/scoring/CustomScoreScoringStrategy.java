package com.yu.ymind.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yu.ymind.model.dto.question.QuestionContentDTO;
import com.yu.ymind.model.entity.App;
import com.yu.ymind.model.entity.Question;
import com.yu.ymind.model.entity.ScoringResult;
import com.yu.ymind.model.entity.UserAnswer;
import com.yu.ymind.model.vo.QuestionVO;
import com.yu.ymind.service.QuestionService;
import com.yu.ymind.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private ScoringResultService scoringResultService;

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
                        .orderByDesc(ScoringResult::getResultScoreRange)// 添加排序条件，按照resultScoreRange字段降序排序
        );
        // 2. 统计用户的总得分
        int totalScore = 0;
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
                        int score = Optional.of(option.getScore()).orElse(0);//获取option对象的score属性值，并将其转换为整数类型。如果score属性值为null，则返回默认值0。
                        totalScore += score;
                    }
                }
            }
        }
        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }

        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;

    }
}
