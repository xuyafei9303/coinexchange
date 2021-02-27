package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ixyf.domain.AccountDetail;
import com.ixyf.service.AccountDetailService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.Account;
import com.ixyf.mapper.AccountMapper;
import com.ixyf.service.AccountService;
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{

    @Resource
    private AccountDetailService accountDetailService;

    /**
     * 审核通过 进行转账
     *
     * @param adminId
     * @param userId
     * @param coinId
     * @param orderNum
     * @param num
     * @param fee
     * @return
     */
    @Override
    public boolean transferAccount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction) {
        Account coinAccount = getCoinAccount(coinId, userId);
        if (coinAccount == null) {
            throw new IllegalArgumentException("该币种的余额为空");
        }
        // 添加一条流水记录
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCoinId(coinId);
        accountDetail.setUserId(userId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setOrderId(orderNum);
        accountDetail.setAccountId(adminId);
        accountDetail.setRefAccountId(adminId );
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(direction);
        accountDetail.setCreated(new Date());

        boolean save = accountDetailService.save(accountDetail);
        if (save) { // 用于余额的增加
            coinAccount.setBalanceAmount(coinAccount.getBalanceAmount().add(num));
            return updateById(coinAccount);
        }
        return save;
    }

    /**
     * 获取用户的某种币的资产
     *
     * @param coinId
     * @param userId
     * @return
     */
    private Account getCoinAccount(Long coinId, Long userId) {

        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getCoinId, coinId)
                .eq(Account::getUserId, userId)
                .eq(Account::getStatus, 1)
        );
    }
}
