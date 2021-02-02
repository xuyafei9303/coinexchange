package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.SmsMapper;
import com.ixyf.domain.Sms;
import com.ixyf.service.SmsService;
@Service
public class SmsServiceImpl extends ServiceImpl<SmsMapper, Sms> implements SmsService{

}
