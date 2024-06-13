package com.yu.ymind.model.vo;

import cn.hutool.json.JSONUtil;
import com.yu.ymind.model.entity.FriendShip;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户好友关系视图
 *
 *
 */
@Data
public class FriendShipVO implements Serializable {

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
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;



    /**
     * 封装类转对象
     *
     * @param friendShipVO
     * @return
     */
    public static FriendShip voToObj(FriendShipVO friendShipVO) {
        if (friendShipVO == null) {
            return null;
        }
        FriendShip friendShip = new FriendShip();
        BeanUtils.copyProperties(friendShipVO, friendShip);
        List<String> tagList = friendShipVO.getTagList();
        return friendShip;
    }

    /**
     * 对象转封装类
     *
     * @param friendShip
     * @return
     */
    public static FriendShipVO objToVo(FriendShip friendShip) {
        if (friendShip == null) {
            return null;
        }
        FriendShipVO friendShipVO = new FriendShipVO();
        BeanUtils.copyProperties(friendShip, friendShipVO);
        return friendShipVO;
    }
}
