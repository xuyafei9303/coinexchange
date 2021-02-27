package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.domain.CashRechargeAuditRecord;

public interface CashRechargeService extends IService<CashRecharge>{

    /**
     * 分页查询现金充值数据
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
    Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    /**
     * 现金充值审核
     * @param userId 审核人
     * @param cashRechargeAuditRecord 审核数据
     * @return
     */
    boolean cashRechargeAudit(Long userId, CashRechargeAuditRecord cashRechargeAuditRecord);

    /**
     * 查询当前用户的充值状态
     * @param page
     * @param userId
     * @param status
     * @return
     */
    Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long userId, Byte status);
}
