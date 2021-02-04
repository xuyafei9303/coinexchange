package com.ixyf.controller;

import cn.hutool.http.HttpUtil;
import com.ixyf.geetest.GeetestLib;
import com.ixyf.geetest.entity.GeetestLibResult;
import com.ixyf.geetest.enums.DigestmodEnum;
import com.ixyf.model.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/gt")
public class GeetestController {

    @Resource
    private GeetestLib geetestLib;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/register")
    @ApiOperation(value = "获取极验的第一次数据包")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "用户验证的一个凭证")
    })
    public R<String> register(String uuid) {
        Map<String, String> paramMap = new HashMap<String, String>();
        DigestmodEnum digestmodEnum = DigestmodEnum.MD5;
        paramMap.put("digestmod", digestmodEnum.getName());
        paramMap.put("user_id", uuid);
        paramMap.put("client_type", "web");
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        paramMap.put("ip_address", HttpUtil.getClientIP(requestAttributes.getRequest()));
        GeetestLibResult result = geetestLib.register(digestmodEnum, paramMap);

        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY, result.getStatus(), 180, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(GeetestLib.GEETEST_SERVER_USER_KEY + ":" + uuid, uuid, 180, TimeUnit.SECONDS);

        return R.ok(result.getData());
    }

}
