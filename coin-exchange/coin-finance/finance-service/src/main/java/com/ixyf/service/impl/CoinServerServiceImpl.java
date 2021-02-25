package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.CoinServerMapper;
import com.ixyf.domain.CoinServer;
import com.ixyf.service.CoinServerService;
@Service
public class CoinServerServiceImpl extends ServiceImpl<CoinServerMapper, CoinServer> implements CoinServerService{

}
