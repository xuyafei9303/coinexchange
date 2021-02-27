package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ixyf.domain.AccountDetail;
import com.ixyf.domain.Coin;
import com.ixyf.domain.Config;
import com.ixyf.service.AccountDetailService;
import com.ixyf.service.CoinService;
import com.ixyf.service.ConfigService;
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

    @Resource
    private CoinService coinService;

    @Resource
    private ConfigService _configService;

    /**
     * 给用户扣减钱
     *
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
    @Override
    public boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction) {

        Account coinAccount = getCoinAccount(coinId, userId);
        if (coinAccount == null) {
            throw new IllegalArgumentException("账户不存在");
        }
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCoinId(coinId);
        accountDetail.setUserId(userId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setAccountId(coinAccount.getId());
        accountDetail.setRefAccountId(coinAccount.getId());
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(direction);

        boolean save = accountDetailService.save(accountDetail);
        if (save) { // 新增流水记录
            BigDecimal balanceAmount = coinAccount.getBalanceAmount();
            BigDecimal decimal = balanceAmount.add(num.multiply(BigDecimal.valueOf(-1)));
            if (decimal.compareTo(BigDecimal.ZERO) > 0) {
                coinAccount.setBalanceAmount(decimal);
                return updateById(coinAccount);
            } else {
                throw new IllegalArgumentException("余额不足");
            }
        }
        return false;
    }

    /**
     * 根据用户查询用户资产
     *
     * @param userId   用户id
     * @param coinName 币种名称
     * @return 货币资产
     */
    @Override
    public Account findByUserAndCoin(Long userId, String coinName) {
        Coin coin = coinService.getCoinByCoinName(coinName);
        if (coin == null) {
            throw new IllegalArgumentException("货币不存在");
        }
        Account account = getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId).eq(Account::getCoinId, coin.getId()));
        if (account == null) {
            throw new IllegalArgumentException("该资产不存在");
        }
        Config buyRateConfigByCode = _configService.getConfigByCode("CNY2USDT");
        account.setBuyRate(new BigDecimal(buyRateConfigByCode.getValue())); // 买进比率

        Config sellRateConfigByCode = _configService.getConfigByCode("USDT2CNY");
        account.setSellRate(new BigDecimal(sellRateConfigByCode.getValue())); // 售出比率

        return account;
    }

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
