package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.UserAccountFreezeMapper;
import com.ixyf.domain.UserAccountFreeze;
import com.ixyf.service.UserAccountFreezeService;
@Service
public class UserAccountFreezeServiceImpl extends ServiceImpl<UserAccountFreezeMapper, UserAccountFreeze> implements UserAccountFreezeService{

}
