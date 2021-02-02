package com.ixyf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.ixyf.domain.SysMenu;
import com.ixyf.feign.JwtToken;
import com.ixyf.feign.OAuth2FeignClient;
import com.ixyf.model.LoginResult;
import com.ixyf.service.SysLoginService;
import com.ixyf.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private OAuth2FeignClient auth2FeignClient;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Override
    public LoginResult login(String username, String password) {
        log.info("用户开始登录：{}", username);

        // 1.先要获取token
        ResponseEntity<JwtToken> feignClientToken = auth2FeignClient.getToken("password", username, password, "admin_type", basicToken);
        if (feignClientToken.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken jwtToken = feignClientToken.getBody();
        log.info("远程调用授权服务器成功，获取的token为： {}", JSON.toJSONString(jwtToken, true));
        assert jwtToken != null;
        String token = jwtToken.getAccessToken();
        System.out.println("accessToken = " + token);

        // 解析token 拿userId
        Jwt jwt = JwtHelper.decode(token);
        String jwtClaims = jwt.getClaims(); // json值
        JSONObject jwtJson = JSON.parseObject(jwtClaims);
        Long user_name_id = Long.valueOf(jwtJson.getString("user_name"));
        // 2.查询菜单数据
        List<SysMenu> menus = sysMenuService.getMenusByUserId(user_name_id);
        // 3.查询权限数据 权限数据已经在jwt里面包含了
        JSONArray authoritiesList = jwtJson.getJSONArray("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
                .map(auth -> new SimpleGrantedAuthority(auth.toString()))
                .collect(Collectors.toList());

        // 将该token存储在redis中，配合网关做jwt校验
        stringRedisTemplate.opsForValue().set(token, "", jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        return new LoginResult(jwtToken.getTokenType() + " " + token, menus, authorities);
    }
}
