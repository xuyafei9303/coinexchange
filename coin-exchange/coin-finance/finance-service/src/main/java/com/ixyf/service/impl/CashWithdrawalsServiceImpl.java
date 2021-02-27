package com.ixyf.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashWithdrawAuditRecord;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.UserServiceFeign;
import com.ixyf.mapper.CashWithdrawAuditRecordMapper;
import com.ixyf.service.AccountService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
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
}
