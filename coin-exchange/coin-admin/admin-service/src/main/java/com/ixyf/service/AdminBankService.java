package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.dto.AdminBankDto;

import java.util.List;

public interface AdminBankService extends IService<AdminBank>{


    /**
     * 根据条件分页展示银行卡
     * @param page 分页参数
     * @param bankCard 银行卡号
     * @return
     */
    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);

    /**
     * 查询所有的银行卡信息
     * @return
     */
    List<AdminBankDto> getAllAdminBanks();
}

