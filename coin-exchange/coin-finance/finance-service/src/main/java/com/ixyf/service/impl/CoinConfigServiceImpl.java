package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CoinConfig;
import com.ixyf.mapper.CoinConfigMapper;
import com.ixyf.service.CoinConfigService;
@Service
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigMapper, CoinConfig> implements CoinConfigService{

}