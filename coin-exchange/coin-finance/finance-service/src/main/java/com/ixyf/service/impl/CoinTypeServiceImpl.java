package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CoinType;
import com.ixyf.mapper.CoinTypeMapper;
import com.ixyf.service.CoinTypeService;
@Service
public class CoinTypeServiceImpl extends ServiceImpl<CoinTypeMapper, CoinType> implements CoinTypeService{

}
