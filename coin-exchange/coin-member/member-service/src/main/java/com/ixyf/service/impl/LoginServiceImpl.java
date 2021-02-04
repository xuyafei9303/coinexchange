package com.ixyf.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ixyf.feign.JwtToken;
import com.ixyf.feign.OAuth2FeignClient;
import com.ixyf.form.LoginForm;
import com.ixyf.form.LoginUser;
import com.ixyf.geetest.GeetestLib;
import com.ixyf.geetest.entity.GeetestLibResult;
import com.ixyf.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private OAuth2FeignClient auth2FeignClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GeetestLib geetestLib;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    // TODO 密码的问题 第一次登录的密码为123456，然后前端会加密为MD5格式的，通过F12可看到，拿到这个密码以后，自己通过BCryptPasswordEncoder进行一下加密，把加密生成的密码更新到数据库对应的用户密码中，就可以登录成功了
    @Override
    public LoginUser login(LoginForm loginForm) {
        log.info("用户: {}，开始登录", loginForm.getUsername());
        checkFormData(loginForm);
        LoginUser loginUser = null;
        // 使用用户名和密码换取token 远程调用authorization-server来获取
        ResponseEntity<JwtToken> responseEntity = auth2FeignClient.getToken("password", loginForm.getUsername(), loginForm.getPassword(), "member_type", basicToken);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JwtToken jwtToken = responseEntity.getBody();
            log.info("远程调用authorization-server成功了，结果为：{}", JSON.toJSONString(jwtToken, true));
            assert jwtToken != null;
            loginUser = new LoginUser(loginForm.getUsername(), jwtToken.getTokenType() + " " + jwtToken.getAccessToken(), jwtToken.getRefreshToken(), jwtToken.getExpiresIn());
            // 使用网关解决登出的问题
            redisTemplate.opsForValue().set(jwtToken.getAccessToken(), "", jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        }
        return loginUser;
    }

    /**
     * 数据校验
     * @param loginForm
     */
    private void checkFormData(LoginForm loginForm) {

        String challenge = loginForm.getGeetest_challenge();
        String validate = loginForm.getGeetest_validate();
        String seccode = loginForm.getGeetest_seccode();
        int status = 0;
        String userId = "";
        // 检测存入redis中的极验云状态标识
        String key = redisTemplate.opsForValue().get(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY);
        assert key != null;
        status = Integer.parseInt(key);
        userId = redisTemplate.opsForValue().get(GeetestLib.GEETEST_SERVER_USER_KEY + ":" + loginForm.getUuid());
        GeetestLibResult result = null;
        assert result != null;
        if (status == 1) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("user_id", userId);
            paramMap.put("client_type", "web");
            paramMap.put("ip_address", HttpUtil.getClientIP(servletRequestAttributes.getRequest()));
            result = geetestLib.successValidate(challenge, validate, seccode, paramMap);
            log.info("验证的结果为：{}", JSON.toJSONString(result, true));
        } else {
            result = geetestLib.failValidate(challenge, validate, seccode);
        }
        // 注意，不要更改返回的结构和值类型
        if (result.getStatus() != 1) {
            log.info("验证码验证异常:", JSON.toJSONString(result, true));
            throw new IllegalArgumentException("验证码验证异常");
        }
    }
}
