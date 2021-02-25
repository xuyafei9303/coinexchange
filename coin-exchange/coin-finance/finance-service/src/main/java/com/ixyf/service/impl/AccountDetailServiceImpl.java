package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.AccountDetail;
import com.ixyf.mapper.AccountDetailMapper;
import com.ixyf.service.AccountDetailService;
@Service
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailMapper, AccountDetail> implements AccountDetailService{

}
