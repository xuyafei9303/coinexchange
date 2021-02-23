package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.UserBankMapper;
import com.ixyf.domain.UserBank;
import com.ixyf.service.UserBankService;
@Service
public class UserBankServiceImpl extends ServiceImpl<UserBankMapper, UserBank> implements UserBankService{

    @Override
    public UserBank getCurrentUserBank(Long userId) {
        return getOne(
                new LambdaQueryWrapper<UserBank>()
                        .eq(UserBank::getUserId, userId)
                        .eq(UserBank::getStatus, 1)
        );
    }

    @Override
    public Page<UserBank> findByPage(Page<UserBank> page, Long usrId) {
        return page(page, new LambdaQueryWrapper<UserBank>()
                .eq(usrId != null, UserBank::getUserId, usrId)
        );
    }
}
