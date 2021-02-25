package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.Coin;
import com.ixyf.mapper.CoinMapper;
import com.ixyf.service.CoinService;
@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService{

}
