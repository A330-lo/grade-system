package com.scs.service;

import com.scs.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 登录信息
     */
    LoginVO login(String username, String password, String role);

    /**
     * 退出登录
     */
    void logout();
}
