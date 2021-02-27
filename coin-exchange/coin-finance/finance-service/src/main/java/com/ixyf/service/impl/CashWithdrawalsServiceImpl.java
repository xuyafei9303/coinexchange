package com.ixyf.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Account;
import com.ixyf.domain.CashWithdrawAuditRecord;
import com.ixyf.domain.Config;
import com.ixyf.dto.UserBankDto;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.UserBankServiceFeign;
import com.ixyf.feign.UserServiceFeign;
import com.ixyf.mapper.CashWithdrawAuditRecordMapper;
import com.ixyf.model.CashSellParam;
import com.ixyf.service.AccountService;
import com.ixyf.service.ConfigService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.CashWithdrawalsMapper;
import com.ixyf.domain.CashWithdrawals;
import com.ixyf.service.CashWithdrawalsService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CashWithdrawalsServiceImpl extends ServiceImpl<CashWithdrawalsMapper, CashWithdrawals> implements CashWithdrawalsService{

    @Resource
    private UserServiceFeign userServiceFeign;

    @Resource
    private AccountService accountService;

    @Resource
    private CashWithdrawAuditRecordMapper cashWithdrawAuditRecordMapper;

    @Resource
    private ConfigService configService;

    @Resource
    private UserBankServiceFeign userBankServiceFeign;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @CreateCache(name = "CASH_WITHDRAWALS_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> lock;

    /**
     * 根据条件分页查询提现记录
     *
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
    @Override
    public Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        Map<Long, UserDto> basicUsers = null;
        LambdaQueryWrapper<CashWithdrawals> queryWrapper = new LambdaQueryWrapper<>();
        // 有用户id时 携带了用户信息
        if (userId != null || StringUtils.isEmpty(userName) || StringUtils.isEmpty(mobile)) {
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Collections.singletonList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) {
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            queryWrapper.in(CashWithdrawals::getUserId, userIds);
        }
        // 其他条件的查询
        queryWrapper.eq(CashWithdrawals::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashWithdrawals::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CashWithdrawals::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<CashWithdrawals> withdrawalsPage = page(page, queryWrapper);
        List<CashWithdrawals> records = withdrawalsPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(CashWithdrawals::getUserId).collect(Collectors.toList());
            if (basicUsers == null) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashWithdrawals -> {
                UserDto userDto = finalBasicUsers.get(cashWithdrawals.getUserId());
                if (userDto != null) {
                    cashWithdrawals.setUsername(userDto.getUsername());
                    cashWithdrawals.setRealName(userDto.getRealName());
                }
            });
        }
        return withdrawalsPage;
    }

    /**
     * 场外交易审核提现记录
     *
     * @param userId
     * @param cashWithdrawAuditRecord
     * @return
     */
    @Override
    public boolean updateWithdrawalsStatus(Long userId, CashWithdrawAuditRecord cashWithdrawAuditRecord) {
        // 使用锁
        boolean lockAndRun = lock.tryLockAndRun(cashWithdrawAuditRecord.getId() + "", 300, TimeUnit.SECONDS, () -> {

            CashWithdrawals cashWithdrawals = getById(cashWithdrawAuditRecord.getId());
            if (cashWithdrawals == null) {
                throw new IllegalArgumentException("现金审核记录不存在");
            }
            // 添加一个审核记录
            CashWithdrawAuditRecord withdrawAuditRecord = new CashWithdrawAuditRecord();
            withdrawAuditRecord.setAuditUserId(userId);
            withdrawAuditRecord.setRemark(cashWithdrawAuditRecord.getRemark());
            withdrawAuditRecord.setCreated(new Date());
            withdrawAuditRecord.setStatus(cashWithdrawAuditRecord.getStatus());
            int step = cashWithdrawals.getStep() + 1;
            withdrawAuditRecord.setStep((byte) step);
            withdrawAuditRecord.setOrderId(cashWithdrawals.getId());

            // 记录保存成功
            int insert = cashWithdrawAuditRecordMapper.insert(withdrawAuditRecord);
            if (insert > 0) {
                cashWithdrawals.setStatus(cashWithdrawAuditRecord.getStatus());
                cashWithdrawals.setRemark(cashWithdrawAuditRecord.getRemark());
                cashWithdrawals.setLastTime(new Date());
                cashWithdrawals.setAccountId(userId);
                cashWithdrawals.setStep((byte) step);

                boolean update = updateById(cashWithdrawals);
                if (update) { // 审核通过 or 审核拒绝
                    boolean decrease =  accountService.decreaseAccountAmount(
                            userId, cashWithdrawals.getUserId(), cashWithdrawals.getCoinId(), cashWithdrawals.getId(), cashWithdrawals.getNum(), cashWithdrawals.getFee(), cashWithdrawals.getRemark(), "withdrawals_out", (byte)2);
                }
            }
        });
        return false;
    }

    /**
     * 查询当前用户的提现记录
     *
     * @param page
     * @param userId
     * @param status
     * @return
     */
    @Override
    public Page<CashWithdrawals> findUserCashWithdrawals(Page<CashWithdrawals> page, Long userId, Byte status) {
        return page(page, new LambdaQueryWrapper<CashWithdrawals>().eq(CashWithdrawals::getUserId, userId).eq(status != null, CashWithdrawals::getStatus, status));
    }

    /**
     * GCN卖出操作
     *
     * @param userId
     * @param cashSellParam
     * @return
     */
    @Override
    public boolean sell(Long userId, CashSellParam cashSellParam) {

        Map<Long, UserDto> basicUsers = userServiceFeign.getBasicUsers(Arrays.asList(userId), null, null);
        if (CollectionUtils.isEmpty(basicUsers)) {
            throw new IllegalArgumentException("用户id错误");
        }
        UserDto userDto = basicUsers.get(userId);

        // 参数校验
        checkCashSellParam(userId, cashSellParam);
        // 手机验证码 支付密码
        validatePhoneCode(userDto.getMobile(), cashSellParam.getValidateCode());
        checkPayPassword(userDto.getPayPassword(), cashSellParam.getPayPassword());

        // 远程调用查询用户银行卡
        UserBankDto userBankInfo = userBankServiceFeign.getUserBankInfo(userId);
        if (userBankInfo == null) {
            throw new IllegalArgumentException("该用户暂未绑定银行卡");
        }

        String remark = RandomUtil.randomNumbers(6);

        // 获取金额
        BigDecimal amount = getCashWithrawalsAmount(cashSellParam.getNum());
        // 手续费
        BigDecimal fee = getCashWithdrawalsFee(amount);
        // 查询用户的账户id
        Account account = accountService.findByUserAndCoin(userId, "GCN");

        // 订单创建
        CashWithdrawals cashWithdrawals = new CashWithdrawals();
        cashWithdrawals.setUserId(userId);
        cashWithdrawals.setBank(userBankInfo.getBank());
        cashWithdrawals.setBankCard(userBankInfo.getBankCard());
        cashWithdrawals.setBankAddr(userBankInfo.getBankAddr());
        cashWithdrawals.setBankProv(userBankInfo.getBankProv());
        cashWithdrawals.setBankCity(userBankInfo.getBankCity());
        cashWithdrawals.setRealName(userBankInfo.getRealName());
        cashWithdrawals.setRemark(remark);
        cashWithdrawals.setStatus((byte) 0);
        cashWithdrawals.setStep((byte) 1);
        cashWithdrawals.setMum(amount.subtract(fee)); // 实际金额 = amount - fee
        cashWithdrawals.setFee(fee);
        cashWithdrawals.setAccountId(account.getId());
        boolean save = save(cashWithdrawals); // 保存
        if (save) {
            // TODO 扣减总资产
        }
        // 锁定一部分账户余额
        return false;
    }

    /**
     * 计算手续费
     * @param amount
     * @return
     */
    private BigDecimal getCashWithdrawalsFee(BigDecimal amount) {
        // 通过总金额乘以费率得到手续费 大金额下使用
        // 如果金额较小，则默认是设置好的最小提现手续费
        Config minPoundage = configService.getConfigByCode("WITHDRAW_MIN_POUNDAGE"); // 最小提现费率
        Config poundageRate = configService.getConfigByCode("WITHDRAW_POUNDAGE_RATE"); // 费率
        BigDecimal withdrawalsMinFee = new BigDecimal(minPoundage.getValue());

        BigDecimal poundageFee = amount.multiply(new BigDecimal(poundageRate.getValue())).setScale(2, RoundingMode.HALF_UP); // 通过费率计算得到的手续费
        // 谁大取谁
        return poundageFee.min(withdrawalsMinFee).equals(poundageFee) ? withdrawalsMinFee : poundageFee;
    }

    /**
     * 通过数量来计算获取金额
     * @param num
     * @return
     */
    private BigDecimal getCashWithrawalsAmount(BigDecimal num) {

        Config rate = configService.getConfigByCode("USDT2CNY");
        return num.multiply(new BigDecimal(rate.getValue())).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 校验支付密码
     * @param userDbPayPassword  用户的支付密码
     * @param inPayPassword 传进来的支付密码
     */
    private void checkPayPassword(String userDbPayPassword, String inPayPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(userDbPayPassword, inPayPassword);
        if (matches) {
            throw new IllegalArgumentException("支付密码错误");
        }
    }

    /**
     * 校验验证码
     * @param mobile
     * @param validateCode
     */
    private void validatePhoneCode(String mobile, String validateCode) {
        // 验证：SMS:CASH_WITHDRAWALS:mobile
        String code = stringRedisTemplate.opsForValue().get("SMS:CASH_WITHDRAWALS:" + mobile);
        if (!validateCode.equals(code)) {
            throw new IllegalArgumentException("验证码错误");
        }
    }

    /**
     * 参数校验
     * @param cashSellParam
     */
    private void checkCashSellParam(Long userId, CashSellParam cashSellParam) {

        Config withdrawStatus = configService.getConfigByCode("WITHDRAW_STATUS");
        if (Integer.parseInt(withdrawStatus.getValue()) != 1) {
            throw new IllegalArgumentException("提现开关暂未开启");
        }
        // 手机支付密码 手机验证码 提现相关
        @NotNull BigDecimal sellParamNum = cashSellParam.getNum();

        // 最小提现额度
        Config withdrawMinAmount = configService.getConfigByCode("WITHDRAW_MIN_AMOUNT");
        if (sellParamNum.compareTo(new BigDecimal(withdrawMinAmount.getValue())) < 0) {
            throw new IllegalArgumentException("达到可提现最小金额,请检查提现金额");
        }
        // 最小提现额度
        Config withdrawMaxAmount = configService.getConfigByCode("WITHDRAW_MAX_AMOUNT");
        if (sellParamNum.compareTo(new BigDecimal(withdrawMaxAmount.getValue())) > 0) {
            throw new IllegalArgumentException("超过了可提现最大金额,请检查提现金额");
        }
    }
}
