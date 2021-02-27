package com.ixyf.service;

import com.ixyf.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface AccountService extends IService<Account>{

    /**
     * 审核通过 进行转账
     * @param adminId
     * @param userId
     * @param coinId
     * @param num
     * @param fee
     * @param remark
     * @return
     */
    boolean transferAccount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction);
}
