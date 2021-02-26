package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.UserServiceFeign;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.CoinRechargeMapper;
import com.ixyf.domain.CoinRecharge;
import com.ixyf.service.CoinRechargeService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CoinRechargeServiceImpl extends ServiceImpl<CoinRechargeMapper, CoinRecharge> implements CoinRechargeService{

    @Resource
    private UserServiceFeign userServiceFeign;

    /**
     * 根据条件分页查询充币记录
     *
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
    @Override
    public Page<CoinRecharge> findByPage(Page<CoinRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CoinRecharge> queryWrapper = new LambdaQueryWrapper<>();

        // 如果本次查询中带了用户信息userId / userName / mobile ,本质就是要把用户的id 放在查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) { // 使用用户的信息进行查询
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Collections.singletonList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) { // 找不到该类用户
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();// 远程调用查询用户
            queryWrapper.in(!CollectionUtils.isEmpty(userIds), CoinRecharge::getUserId, userIds);
        }
        // 如果本次查询中没有带用户信息
        queryWrapper
                .eq(coinId != null, CoinRecharge::getCoinId, coinId)
                .eq(status != null, CoinRecharge::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CoinRecharge::getAmount,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)

                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CoinRecharge::getCreated,
                        startTime, endTime + " 23:59:59"
                );

        Page<CoinRecharge> coinRechargePage = page(page, queryWrapper);
        List<CoinRecharge> records = coinRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            if (CollectionUtils.isEmpty(basicUsers)) {
                List<Long> ids = records.stream().map(CoinRecharge::getUserId).collect(Collectors.toList());
                basicUsers = userServiceFeign.getBasicUsers(ids, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(coinRecharge -> {
                UserDto userDto = finalBasicUsers.get(coinRecharge.getUserId());
                if (userDto != null) {
                    coinRecharge.setUsername(userDto.getUsername());
                    coinRecharge.setRealName(userDto.getRealName());
                }
            });
        }
        return coinRechargePage;
    }
}
