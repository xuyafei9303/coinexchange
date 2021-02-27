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

    /**
     * 给用户扣减钱
     * @param adminId
     * @param userId
     * @param coinId
     * @param orderNum
     * @param num
     * @param fee
     * @param remark
     * @param businessType
     * @param direction
     * @return
     */
    boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction);

    /**
     * 根据用户查询用户资产
     * @param userId 用户id
     * @param coinName 币种名称
     * @return 货币资产
     */
    Account findByUserAndCoin(Long userId, String coinName);
}
