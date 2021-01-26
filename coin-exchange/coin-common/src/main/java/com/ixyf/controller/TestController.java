package com.ixyf.controller;

import com.ixyf.model.R;
import com.ixyf.model.WebLog;
import com.ixyf.service.TestService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Api(tags = "coin-common的测试接口")
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TestService testService;

    @GetMapping("/common/test")
    @ApiOperation(value = "测试方法testMethod()", authorizations = {@Authorization("Authorization")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "param1", value = "参数1", dataType = "String", paramType = "query", example = "示例1"),
            @ApiImplicitParam(name = "param2", value = "参数2", dataType = "String", paramType = "query", example = "示例2")
    })
    public R<String> testMethod(String param1, String param2) {
        return R.ok("ok");
    }

    @GetMapping("/date/test")
    @ApiOperation(value = "日期格式的测试", authorizations = {@Authorization("Authorization")})
    public R<Date> testDate() {
        return R.ok(new Date());
    }

    @GetMapping("/redis/test")
    @ApiOperation(value = "redis测试", authorizations = {@Authorization("Authorization")})
    public R<String> testRedis() {
        final WebLog webLog = new WebLog();
        webLog.setResult("ok!");
        webLog.setUsername("xuyafei");
        webLog.setIp("127.0.0.1");
        redisTemplate.opsForValue().set("com.ixyf.domain.WebLog", webLog);
        return R.ok("ok");
    }

    @GetMapping("/jetcache/test")
    @ApiOperation(value = "缓存的测试", authorizations = {@Authorization("Authorization")})
    public R<WebLog> testJetCache(String username) {
        final WebLog webLog = testService.queryByUsername(username);
        System.out.println("webLog = " + webLog);
        return R.ok(webLog);
    }
}
