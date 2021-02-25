package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.ForexCoinMapper;
import com.ixyf.domain.ForexCoin;
import com.ixyf.service.ForexCoinService;
@Service
public class ForexCoinServiceImpl extends ServiceImpl<ForexCoinMapper, ForexCoin> implements ForexCoinService{

}
