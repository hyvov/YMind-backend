package com.yu.ymind.model.dto.friendship;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑用户好友关系请求
 *
 *
 */
@Data
public class FriendShipEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 好友id
     */
    private Long friendId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 好友状态：0-好友关系正常, 1-好友已删除, 2-已拉黑好友，3-好友请求待处理 ,4-好友请求被拒绝
     */
    private Integer status;



    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}