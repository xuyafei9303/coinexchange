package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashWithdrawAuditRecord;
import com.ixyf.domain.CashWithdrawals;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.model.CashSellParam;

public interface CashWithdrawalsService extends IService<CashWithdrawals>{


    /**
     * 根据条件分页查询提现记录
     * @param page
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
    Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    /**
     * 场外交易审核提现记录
     * @param userId
     * @param cashWithdrawAuditRecord
     * @return
     */
    boolean updateWithdrawalsStatus(Long userId, CashWithdrawAuditRecord cashWithdrawAuditRecord);

    /**
     * 查询当前用户的提现记录
     * @param page
     * @param userId
     * @param status
     * @return
     */
    Page<CashWithdrawals> findUserCashWithdrawals(Page<CashWithdrawals> page, Long userId, Byte status);

    /**
     * GCN卖出操作
     * @param userId
     * @param cashSellParam
     * @return
     */
    boolean sell(Long userId, CashSellParam cashSellParam);
}
