package com.ixyf.service;

import com.ixyf.model.LoginResult;

/**
 * 登录接口
 */
public interface SysLoginService {

    /**
     * 登录接口
     * @param username // 用户名
     * @param password // 用户密码
     * @return
     */
    LoginResult login(String username, String password);
}
