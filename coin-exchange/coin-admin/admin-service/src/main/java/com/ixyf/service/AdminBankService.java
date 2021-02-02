package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AdminBankService extends IService<AdminBank>{


    /**
     * 根据条件分页展示银行卡
     * @param page 分页参数
     * @param bankCard 银行卡号
     * @return
     */
    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);
}

