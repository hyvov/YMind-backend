package com.yu.ymind.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yu.ymind.model.dto.question.QuestionContentDTO;
import com.yu.ymind.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 问题视图
 *
 *
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

//    /**
//     * 题目内容（json格式）
//     */
//    private String questionContent;
    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questionContent;
    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        // 如果传入的questionVO为空，直接返回null
        if (questionVO == null) {
            return null;
        }
        // 创建一个新的Question对象
        Question question = new Question();
        // 使用BeanUtils工具类，将questionVO的属性值拷贝到question对象中
        BeanUtils.copyProperties(questionVO, question);
        // 获取questionVO的questionContent属性值，List<QuestionContentDTO>类型的对象
        List<QuestionContentDTO> questionContentDTO = questionVO.getQuestionContent();
        // 将questionContentDTO对象转换为JSON格式的字符串，并设置到question对象的questionContent属性中
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        // 返回question对象
        return question;
    }

    /**
     * 对象转封装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
//             return questionVO;
        String questionContent = question.getQuestionContent();//从question对象中获取question内容
        if (questionContent != null) {
            //使用toList将JSON 字符串转换为指定类型的列表，并使用class将questionContent定义为QuestionContentDTO类
            questionVO.setQuestionContent(JSONUtil.toList(questionContent, QuestionContentDTO.class));
        }
        return questionVO;
    }
}
