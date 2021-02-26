package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashWithdrawals;
import com.ixyf.domain.CoinRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinRechargeService extends IService<CoinRecharge>{


    /**
     * 根据条件分页查询充币记录
     * @param page
     * @param coinId
     * @param userId
     * @param userName
     * @param mobile
     * @param status
     * @param numMin
     * @param numMax
     * @param startTime
     * @param endTime
     * @return
     */
    Page<CoinRecharge> findByPage(Page<CoinRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);
}
