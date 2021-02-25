package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AdminAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AdminAddressService extends IService<AdminAddress>{

    /**
     * 分页查询归集地址
     * @param page
     * @param coinId
     * @return
     */
    Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId);
}
