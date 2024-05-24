package com.yu.ymind.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑问题请求
 *
 *
 */
@Data
public class QuestionEditRequest implements Serializable {


    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO>  questionContent;


    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;


    private static final long serialVersionUID = 1L;
}