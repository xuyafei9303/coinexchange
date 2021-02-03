package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.UserWalletMapper;
import com.ixyf.domain.UserWallet;
import com.ixyf.service.UserWalletService;
@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService{

    @Override
    public Page<UserWallet> findByPage(Page<UserWallet> page, Long userId) {
        return page(page, new LambdaQueryWrapper<UserWallet>().eq(UserWallet::getUserId, userId));
    }
}
