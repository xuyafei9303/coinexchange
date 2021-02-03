package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserAddressService extends IService<UserAddress>{

    /**
     * 分页查询用户的钱包地址信息
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */

    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);
}
