package com.ixyf.controller;

import com.ixyf.domain.Sms;
import com.ixyf.model.R;
import com.ixyf.service.SmsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/sendTo")
    @ApiOperation(value = "发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sms", value = "sms json")
    })
    public R sendSms(@RequestBody @Validated Sms sms) {
        boolean send = smsService.sendSms(sms);
        if (send) {
            return R.ok();
        }
        return R.fail("发送失败");
    }
}
