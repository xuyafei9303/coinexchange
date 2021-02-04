package com.ixyf.controller;

import com.ixyf.form.LoginForm;
import com.ixyf.form.LoginUser;
import com.ixyf.model.R;
import com.ixyf.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "登录控制器")
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @ApiOperation(value = "会员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginForm", value = "loginForm登录表单参数的json数据")
    })
    public R<LoginUser> login(@RequestBody @Validated LoginForm loginForm) {
        LoginUser loginUser = loginService.login(loginForm);
        return R.ok(loginUser);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("e10adc3949ba59abbe56e057f20f883e");
        System.out.println("encode = " + encode);
    }
}
