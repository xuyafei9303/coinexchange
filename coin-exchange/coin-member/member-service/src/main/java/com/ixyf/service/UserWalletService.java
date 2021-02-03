package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserWallet;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserWalletService extends IService<UserWallet>{


    /**
     * 分页展示用户的提币地址
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */
    Page<UserWallet> findByPage(Page<UserWallet> page, Long userId);
}
