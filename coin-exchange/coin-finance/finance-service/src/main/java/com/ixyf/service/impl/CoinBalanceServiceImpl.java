package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CoinBalance;
import com.ixyf.mapper.CoinBalanceMapper;
import com.ixyf.service.CoinBalanceService;
@Service
public class CoinBalanceServiceImpl extends ServiceImpl<CoinBalanceMapper, CoinBalance> implements CoinBalanceService{

}
