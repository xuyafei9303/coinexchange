package com.ixyf.service;

import com.ixyf.domain.Sms;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SmsService extends IService<Sms>{


    /**
     * 发送短信验证码
     * @param sms 短信内容
     * @return
     */
    boolean sendSms(Sms sms);
}
