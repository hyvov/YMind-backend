package com.yu.ymind.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.ymind.annotation.AuthCheck;
import com.yu.ymind.common.BaseResponse;
import com.yu.ymind.common.DeleteRequest;
import com.yu.ymind.common.ErrorCode;
import com.yu.ymind.common.ResultUtils;
import com.yu.ymind.constant.UserConstant;
import com.yu.ymind.exception.BusinessException;
import com.yu.ymind.exception.ThrowUtils;

import com.yu.ymind.model.dto.friendship.FriendShipAddRequest;
import com.yu.ymind.model.dto.friendship.FriendShipEditRequest;
import com.yu.ymind.model.dto.friendship.FriendShipQueryRequest;
import com.yu.ymind.model.dto.friendship.FriendShipUpdateRequest;
import com.yu.ymind.model.entity.FriendShip;
import com.yu.ymind.model.entity.User;
import com.yu.ymind.model.vo.FriendShipVO;
import com.yu.ymind.service.FriendShipService;
import com.yu.ymind.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户好友关系接口
 *
 *
 */
@RestController
@RequestMapping("/friendShip")
@Slf4j
public class FriendShipController {

    @Resource
    private FriendShipService friendShipService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建用户好友关系
     *
     * @param friendShipAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addFriendShip(@RequestBody FriendShipAddRequest friendShipAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(friendShipAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        FriendShip friendShip = new FriendShip();
        BeanUtils.copyProperties(friendShipAddRequest, friendShip);
        // 数据校验
        friendShipService.validFriendShip(friendShip, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        friendShip.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = friendShipService.save(friendShip);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newFriendShipId = friendShip.getId();
        return ResultUtils.success(newFriendShipId);
    }

    /**
     * 删除用户好友关系
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFriendShip(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        FriendShip oldFriendShip = friendShipService.getById(id);
        ThrowUtils.throwIf(oldFriendShip == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldFriendShip.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = friendShipService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户好友关系（仅管理员可用）
     *
     * @param friendShipUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateFriendShip(@RequestBody FriendShipUpdateRequest friendShipUpdateRequest) {
        if (friendShipUpdateRequest == null || friendShipUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        FriendShip friendShip = new FriendShip();
        BeanUtils.copyProperties(friendShipUpdateRequest, friendShip);
        // 数据校验
        friendShipService.validFriendShip(friendShip, false);
        // 判断是否存在
        long id = friendShipUpdateRequest.getId();
        FriendShip oldFriendShip = friendShipService.getById(id);
        ThrowUtils.throwIf(oldFriendShip == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = friendShipService.updateById(friendShip);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户好友关系（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<FriendShipVO> getFriendShipVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        FriendShip friendShip = friendShipService.getById(id);
        ThrowUtils.throwIf(friendShip == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(friendShipService.getFriendShipVO(friendShip, request));
    }

    /**
     * 分页获取用户好友关系列表（仅管理员可用）
     *
     * @param friendShipQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FriendShip>> listFriendShipByPage(@RequestBody FriendShipQueryRequest friendShipQueryRequest) {
        long current = friendShipQueryRequest.getCurrent();
        long size = friendShipQueryRequest.getPageSize();
        // 查询数据库
        Page<FriendShip> friendShipPage = friendShipService.page(new Page<>(current, size),
                friendShipService.getQueryWrapper(friendShipQueryRequest));
        return ResultUtils.success(friendShipPage);
    }

    /**
     * 分页获取用户好友关系列表（封装类）
     *
     * @param friendShipQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<FriendShipVO>> listFriendShipVOByPage(@RequestBody FriendShipQueryRequest friendShipQueryRequest,
                                                               HttpServletRequest request) {
        long current = friendShipQueryRequest.getCurrent();
        long size = friendShipQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FriendShip> friendShipPage = friendShipService.page(new Page<>(current, size),
                friendShipService.getQueryWrapper(friendShipQueryRequest));
        // 获取封装类
        return ResultUtils.success(friendShipService.getFriendShipVOPage(friendShipPage, request));
    }

    /**
     * 分页获取当前登录用户创建的用户好友关系列表
     *
     * @param friendShipQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<FriendShipVO>> listMyFriendShipVOByPage(@RequestBody FriendShipQueryRequest friendShipQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(friendShipQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        friendShipQueryRequest.setUserId(loginUser.getId());
        long current = friendShipQueryRequest.getCurrent();
        long size = friendShipQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FriendShip> friendShipPage = friendShipService.page(new Page<>(current, size),
                friendShipService.getQueryWrapper(friendShipQueryRequest));
        // 获取封装类
        return ResultUtils.success(friendShipService.getFriendShipVOPage(friendShipPage, request));
    }

    /**
     * 编辑用户好友关系（给用户使用）
     *
     * @param friendShipEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editFriendShip(@RequestBody FriendShipEditRequest friendShipEditRequest, HttpServletRequest request) {
        if (friendShipEditRequest == null || friendShipEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        FriendShip friendShip = new FriendShip();
        BeanUtils.copyProperties(friendShipEditRequest, friendShip);
        // 数据校验
        friendShipService.validFriendShip(friendShip, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = friendShipEditRequest.getId();
        FriendShip oldFriendShip = friendShipService.getById(id);
        ThrowUtils.throwIf(oldFriendShip == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldFriendShip.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = friendShipService.updateById(friendShip);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
