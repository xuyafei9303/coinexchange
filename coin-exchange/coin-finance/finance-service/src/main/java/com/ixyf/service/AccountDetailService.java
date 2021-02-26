package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AccountDetail;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AccountDetailService extends IService<AccountDetail>{

    /**
     * 根据条件分页查询资金流水
     * @param page
     * @param coinId
     * @param userId
     * @param userName
     * @param mobile
     * @param amountId
     * @param amountStart
     * @param amountEnd
     * @param startTime
     * @param endTime
     * @return
     */
    Page<AccountDetail> findByPage(Page<AccountDetail> page, Long coinId, Long userId, String userName, String mobile, String amountId, String amountStart, String amountEnd, String startTime, String endTime);
}
