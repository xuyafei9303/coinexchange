package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.AddressPoolMapper;
import com.ixyf.domain.AddressPool;
import com.ixyf.service.AddressPoolService;
@Service
public class AddressPoolServiceImpl extends ServiceImpl<AddressPoolMapper, AddressPool> implements AddressPoolService{

}
