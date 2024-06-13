package com.yu.ymind.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.ymind.model.dto.friendship.FriendShipQueryRequest;
import com.yu.ymind.model.entity.FriendShip;
import com.yu.ymind.model.vo.FriendShipVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户好友关系服务
 *
 *
 */
public interface FriendShipService extends IService<FriendShip> {

    /**
     * 校验数据
     *
     * @param friendShip
     * @param add 对创建的数据进行校验
     */
    void validFriendShip(FriendShip friendShip, boolean add);

    /**
     * 获取查询条件
     *
     * @param friendShipQueryRequest
     * @return
     */
    QueryWrapper<FriendShip> getQueryWrapper(FriendShipQueryRequest friendShipQueryRequest);
    
    /**
     * 获取用户好友关系封装
     *
     * @param friendShip
     * @param request
     * @return
     */
    FriendShipVO getFriendShipVO(FriendShip friendShip, HttpServletRequest request);

    /**
     * 分页获取用户好友关系封装
     *
     * @param friendShipPage
     * @param request
     * @return
     */
    Page<FriendShipVO> getFriendShipVOPage(Page<FriendShip> friendShipPage, HttpServletRequest request);
}
