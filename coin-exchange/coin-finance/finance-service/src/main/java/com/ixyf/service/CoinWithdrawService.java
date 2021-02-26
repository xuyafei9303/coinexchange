package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CoinWithdraw;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinWithdrawService extends IService<CoinWithdraw>{


    /**
     * 数字货币提现记录分页查询
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
    Page<CoinWithdraw> findByPage(Page<CoinWithdraw> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);
}
