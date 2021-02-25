package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.Account;
import com.ixyf.mapper.AccountMapper;
import com.ixyf.service.AccountService;
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{

}
