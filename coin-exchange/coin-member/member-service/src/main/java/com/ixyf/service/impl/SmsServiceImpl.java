package com.ixyf.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyun.mns.common.ClientException;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.ixyf.domain.Config;
import com.ixyf.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.SmsMapper;
import com.ixyf.domain.Sms;
import com.ixyf.service.SmsService;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
@Slf4j
public class SmsServiceImpl extends ServiceImpl<SmsMapper, Sms> implements SmsService{

    @Resource
    private ISmsService iSmsService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean sendSms(Sms sms) {
        log.info("开始发送短信验证码 -> {}", JSON.toJSONString(sms, true));

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = buildRequest(sms);
        SendSmsResponse sendSmsResponse ;
        try {
            sendSmsResponse = iSmsService.sendSmsRequest(request);
            log.info("短信验证码发送的结果为：{}", JSON.toJSONString(sendSmsResponse, true));
            String code = sendSmsResponse.getCode();
            if ("OK".equals(code)) { // 发送成功
                sms.setStatus(1);
                return save(sms);
            }
            return false;
        } catch (ClientException | com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 构建发送的request请求对象
     * @param sms
     * @return
     */
    private SendSmsRequest buildRequest(Sms sms) {
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(sms.getMobile());

        Config signByCode = configService.getConfigByCode("SIGN");
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(signByCode.getValue());
        // 必填:短信模板-可在短信控制台中找到
        Config configByCode = configService.getConfigByCode(sms.getTemplateCode());
        if (configByCode == null) {
            throw new IllegalArgumentException("宁输入的签名不存在");
        }
        request.setTemplateCode(configByCode.getValue());
        // 可选:模板中的变量替换JSON串,如模板内容为"【企业级分布式应用服务】,您的验证码为${code}"时,此处的值为
        String randomNumbers = RandomUtil.randomNumbers(6);
        // 需要先把randomNumbers验证码波存在redis里面
        // key SMS:VERIFY_OLD_PHONE:PHONE_NUMBER    value randomNumbers
        redisTemplate.opsForValue().set("SMS:" + sms.getTemplateCode() + ":" + sms.getMobile(), randomNumbers, 5, TimeUnit.MINUTES);
        request.setTemplateParam("{\"code\":\"" + randomNumbers + "\"}");

        @NotBlank String desc = configByCode.getDesc();
        String content = signByCode.getValue() + ":" + desc.replaceAll("\\$\\{code\\}", randomNumbers);
        sms.setContent(content); // 短信内容 ${sign}验证码：${code}，您正在修改手机号或邮箱。10分钟内有效，请勿告诉他人

        return request;
    }
}
