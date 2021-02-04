package com.ixyf.service;

import com.ixyf.form.LoginForm;
import com.ixyf.form.LoginUser;

public interface LoginService {
    /**
     * 会员登录
     * @param loginForm 登录的表单参数
     * @return
     */
    LoginUser login(LoginForm loginForm);

}
