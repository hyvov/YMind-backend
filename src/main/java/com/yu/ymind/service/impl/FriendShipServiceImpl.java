package com.yu.ymind.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.ymind.common.ErrorCode;
import com.yu.ymind.constant.CommonConstant;
import com.yu.ymind.exception.ThrowUtils;

import com.yu.ymind.mapper.FriendShipMapper;
import com.yu.ymind.model.dto.friendship.FriendShipQueryRequest;
import com.yu.ymind.model.entity.FriendShip;
import com.yu.ymind.model.entity.User;
import com.yu.ymind.model.enums.FriendShipEnum;
import com.yu.ymind.model.vo.FriendShipVO;
import com.yu.ymind.model.vo.UserVO;
import com.yu.ymind.service.FriendShipService;
import com.yu.ymind.service.UserService;
import com.yu.ymind.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户好友关系服务实现
 */
@Service
@Slf4j
public class FriendShipServiceImpl extends ServiceImpl<FriendShipMapper, FriendShip> implements FriendShipService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param friendShip
     * @param add        对创建的数据进行校验
     */
    @Override
    public void validFriendShip(FriendShip friendShip, boolean add) {
        ThrowUtils.throwIf(friendShip == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Integer FriendShipStatus = friendShip.getStatus();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            FriendShipEnum friendShipEnum = FriendShipEnum.getEnumByValue(FriendShipStatus);
            ThrowUtils.throwIf(FriendShipStatus == null, ErrorCode.PARAMS_ERROR, "没有好友信息");
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则

    }

    /**
     * 获取查询条件
     *
     * @param friendShipQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<FriendShip> getQueryWrapper(FriendShipQueryRequest friendShipQueryRequest) {
        QueryWrapper<FriendShip> queryWrapper = new QueryWrapper<>();
        if (friendShipQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long friendId= friendShipQueryRequest.getFriendId();
        Integer status=friendShipQueryRequest.getStatus();

        Long id = friendShipQueryRequest.getId();
        Long notId = friendShipQueryRequest.getNotId();
        String title = friendShipQueryRequest.getTitle();
        String content = friendShipQueryRequest.getContent();
        String searchText = friendShipQueryRequest.getSearchText();
        String sortField = friendShipQueryRequest.getSortField();
        String sortOrder = friendShipQueryRequest.getSortOrder();
        List<String> tagList = friendShipQueryRequest.getTags();
        Long userId = friendShipQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(friendId), "friendId", friendId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户好友关系封装
     *
     * @param friendShip
     * @param request
     * @return
     */
    @Override
    public FriendShipVO getFriendShipVO(FriendShip friendShip, HttpServletRequest request) {
        // 对象转封装类
        FriendShipVO friendShipVO = FriendShipVO.objToVo(friendShip);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = friendShip.getUserId();
        Long friendId= friendShip.getFriendId();
        Integer status=friendShip.getStatus();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        friendShipVO.setUser(userVO);
        friendShipVO.setFriendId(friendId);
        friendShipVO.setStatus(status);
        // 2. 已登录，获取用户点赞、收藏状态
        long friendShipId = friendShip.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        // endregion

        return friendShipVO;
    }

    /**
     * 分页获取用户好友关系封装
     *
     * @param friendShipPage
     * @param request
     * @return
     */
    @Override
    public Page<FriendShipVO> getFriendShipVOPage(Page<FriendShip> friendShipPage, HttpServletRequest request) {
        List<FriendShip> friendShipList = friendShipPage.getRecords();
        Page<FriendShipVO> friendShipVOPage = new Page<>(friendShipPage.getCurrent(), friendShipPage.getSize(), friendShipPage.getTotal());
        if (CollUtil.isEmpty(friendShipList)) {
            return friendShipVOPage;
        }
        // 对象列表 => 封装对象列表
        List<FriendShipVO> friendShipVOList = friendShipList.stream().map(friendShip -> {
            return FriendShipVO.objToVo(friendShip);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = friendShipList.stream().map(FriendShip::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));


        // 填充信息
        friendShipVOList.forEach(friendShipVO -> {
            Long userId = friendShipVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            friendShipVO.setUser(userService.getUserVO(user));

        });
        // endregion

        friendShipVOPage.setRecords(friendShipVOList);
        return friendShipVOPage;
    }

}
