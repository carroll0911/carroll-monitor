package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.dto.UserCacheDto;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.LoginResponse;
import com.carroll.monitor.analyzer.response.OperatorListResponse;
import com.carroll.monitor.analyzer.response.OperatorPageResponse;
import com.carroll.monitor.analyzer.response.OperatorResponse;
import com.carroll.spring.rest.starter.BaseResponse;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public interface IOperatorService {

    OperatorResponse save(OperatorRequest request);

    OperatorResponse update(OperatorUpdateRequest request);

    BaseResponse delete(IdRequest request);

    OperatorPageResponse page(OperatorPageRequest request);

    OperatorListResponse list(BmBaseRequest request);

    /**
     * 登陆
     * @param request
     * @return
     */
    LoginResponse login(LoginRequest request);

    /**
     * 注销登录
     * @return
     */
    BaseResponse logout();

    UserCacheDto getUserCache(String userId);

    /**
     * 修改密码
     * @param req
     */
    void modifyPwd(ModifyPasswordReq req);

    /**
     * 重置密码
     * @param userId
     */
    void resetPwd(String userId);

    /**
     * 修改用户信息
     * @param email
     * @param mobile
     */
    void updateMyInfo(String email, String mobile);
}
