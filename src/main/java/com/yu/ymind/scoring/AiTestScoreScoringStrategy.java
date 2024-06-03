package com.yu.ymind.scoring;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yu.ymind.manager.AIManager;
import com.yu.ymind.model.dto.question.QuestionAnswerDTO;
import com.yu.ymind.model.dto.question.QuestionContentDTO;
import com.yu.ymind.model.entity.App;
import com.yu.ymind.model.entity.Question;
import com.yu.ymind.model.entity.ScoringResult;
import com.yu.ymind.model.entity.UserAnswer;
import com.yu.ymind.model.vo.QuestionVO;
import com.yu.ymind.service.QuestionService;
import com.yu.ymind.service.ScoringResultService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * AI测评类评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoreScoringStrategy implements ScoringStrategy {


    @Resource
    private ScoringResultService scoringResultService;

    @Resource
    private QuestionService questionService;

    @Resource
    private AIManager aiManager;

    @Resource
    private RedissonClient redissonClient;

    //分布式锁的Key
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * 本地缓存
     */
    private final Cache<String, String> answerCacheMap =   //通过泛型实现
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存10分钟移除
                    .expireAfterAccess(10L, TimeUnit.MINUTES)
                    .build();


    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {


        Long appId = app.getId();// 根据应用ID查询题目信息

        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);

        if (StrUtil.isNotBlank(answerJson)) { //如果有缓存，直接返回
            //构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            Thread.sleep(1000); // 假装花费1秒钟
            return userAnswer;

        }
        //定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);//定义锁的对象
        try {
            //竞争锁
            boolean res =lock.tryLock(3,15,TimeUnit.SECONDS);
            //没抢到锁，强行返回
            if (!res){
                return null;
            }
            //抢到锁了，执行后续业务逻辑
            Question question = questionService.getOne(//查询数据库中的一个记录并返回结果
                    Wrappers.lambdaQuery(Question.class)// 创建查询包装器，指定查询实体为Question
                            .eq(Question::getAppId, appId) // 添加查询条件，查询appId字段等于appId参数的记录
            );


            // 2. 统计用户的总得分
            QuestionVO questionVO = QuestionVO.objToVo(question);//将查询到的题目信息转化为脱敏对象
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();//从QuestionVO对象中获取题目内容，并将结果存储在questionContent列表中
            //调用AI获取结果
            //封装prompt
            // 封装 Prompt
            String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);

            // AI 生成
            String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
            // 结果处理
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}");
            String json = result.substring(start, end + 1);

            //构造缓存json结果
            answerCacheMap.put(cacheKey, json);

            // 3. 构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;

        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {//只有本人能释放这个锁
                    lock.unlock(); // 释放锁
                }
            }
        }


    }

    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    /**
     * 构件缓存key
     *
     * @param appId
     * @param choicesStr
     * @return
     */
    private String buildCacheKey(Long appId, String choicesStr) {
        return DigestUtil.md5Hex(appId + ":" + choicesStr);//使用hutool工具类加载缓存key
    }

}
