package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Account;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.UserServiceFeign;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.AccountDetail;
import com.ixyf.mapper.AccountDetailMapper;
import com.ixyf.service.AccountDetailService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailMapper, AccountDetail> implements AccountDetailService{

    @Resource
    private UserServiceFeign userServiceFeign;

    /**
     * 根据条件分页查询资金流水
     *
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
    @Override
    public Page<AccountDetail> findByPage(Page<AccountDetail> page, Long coinId, Long userId, String userName, String mobile, String amountId, String amountStart, String amountEnd, String startTime, String endTime) {
        LambdaQueryWrapper<AccountDetail> queryWrapper = new LambdaQueryWrapper<>();
        Map<Long, UserDto> basicUsers = null;

        // 用户的查询
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) {
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Collections.singletonList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) {
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            queryWrapper.in(AccountDetail::getUserId, userIds);
        }
        queryWrapper
                .eq(amountId != null, AccountDetail::getAccountId, amountId)
                .eq(coinId != null, AccountDetail::getCoinId, coinId)
                .between(
                        !(StringUtils.isEmpty(amountStart) || StringUtils.isEmpty(amountEnd)),
                        AccountDetail::getAmount,
                        new BigDecimal(StringUtils.isEmpty(amountStart) ? "0" : amountStart),
                        new BigDecimal(StringUtils.isEmpty(amountEnd) ? "0": amountEnd)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        AccountDetail::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<AccountDetail> accountDetailPage = page(page, queryWrapper);
        List<AccountDetail> records = accountDetailPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(AccountDetail::getUserId).collect(Collectors.toList());
            if (basicUsers == null) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(accountDetail -> {
                UserDto userDto = finalBasicUsers.get(accountDetail.getUserId());
                accountDetail.setUsername(userDto.getUsername());
                accountDetail.setRealName(userDto.getRealName());
            });
        }
        return accountDetailPage;
    }
}
