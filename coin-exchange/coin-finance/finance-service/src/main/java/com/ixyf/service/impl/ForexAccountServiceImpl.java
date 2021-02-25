package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.ForexAccountMapper;
import com.ixyf.domain.ForexAccount;
import com.ixyf.service.ForexAccountService;
@Service
public class ForexAccountServiceImpl extends ServiceImpl<ForexAccountMapper, ForexAccount> implements ForexAccountService{

}
