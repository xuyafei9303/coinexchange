package com.ixyf.form;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ixyf.geetest.GeetestLib;
import com.ixyf.geetest.entity.GeetestLibResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Data
public class GeetestForm {

    /**
     * 极验数据
     */
    @ApiModelProperty(value = "geetest_validate")
    private String geetest_validate;

    @ApiModelProperty(value = "geetest_seccode")
    private String geetest_seccode;

    @ApiModelProperty(value = "geetest_challenge")
    private String geetest_challenge;

    private String uuid;

    public void check(GeetestForm geetestForm, GeetestLib geetestLib, RedisTemplate<String, Object> redisTemplate) {
        String challenge = geetestForm.getGeetest_challenge();
        String validate = geetestForm.getGeetest_validate();
        String seccode = geetestForm.getGeetest_seccode();
        int status = 0;
        String userId = "";
        // 检测存入redis中的极验云状态标识
        String stringStatus = Objects.requireNonNull(redisTemplate.opsForValue().get(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY)).toString();
        status = Integer.parseInt(stringStatus);
        userId = Objects.requireNonNull(redisTemplate.opsForValue().get(GeetestLib.GEETEST_SERVER_USER_KEY + ":" + geetestForm.getUuid())).toString();
        GeetestLibResult result = null;
        assert false;
        if (status == 1) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Map<String, String> paramMap = new HashMap<>();
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
