package com.yu.ymind.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName friend_ship
 */
@TableName(value ="friend_ship")
@Data
public class FriendShip implements Serializable {
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}