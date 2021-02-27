package com.ixyf.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashRechargeAuditRecord;
import com.ixyf.domain.Coin;
import com.ixyf.domain.Config;
import com.ixyf.dto.AdminBankDto;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.AdminBankServiceFeign;
import com.ixyf.feign.UserServiceFeign;
import com.ixyf.mapper.CashRechargeAuditRecordMapper;
import com.ixyf.model.CashParam;
import com.ixyf.service.AccountService;
import com.ixyf.service.CoinService;
import com.ixyf.service.ConfigService;
import com.ixyf.vo.CashTradeVo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CashRecharge;
import com.ixyf.mapper.CashRechargeMapper;
import com.ixyf.service.CashRechargeService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CashRechargeServiceImpl extends ServiceImpl<CashRechargeMapper, CashRecharge> implements CashRechargeService{

    @Resource
    private UserServiceFeign userServiceFeign;

    @Resource
    private CashRechargeAuditRecordMapper cashRechargeAuditRecordMapper;

    @Resource
    private ConfigService configService;

    @Resource
    private AccountService accountService;

    @Resource
    private AdminBankServiceFeign adminBankServiceFeign;

    @Resource
    private CoinService coinService;

    @Resource
    private Snowflake snowflake;

    @CreateCache(name = "CASH_RECHARGE_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> cache;

    @Override
    public Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {

        LambdaQueryWrapper<CashRecharge> queryWrapper = new LambdaQueryWrapper<>();

        // 如果本次查询中带了用户信息userId / userName / mobile ,本质就是要把用户的id 放在查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId != null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)) { // 使用用户的信息进行查询
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Collections.singletonList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) { // 找不到该类用户
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();// 远程调用查询用户
            queryWrapper.in(!CollectionUtils.isEmpty(userIds), CashRecharge::getUserId, userIds);
        }
        // 如果本次查询中没有带用户信息
        queryWrapper
                .eq(coinId != null, CashRecharge::getCoinId, coinId)
                .eq(status != null, CashRecharge::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashRecharge::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)

                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CashRecharge::getCreated,
                        startTime, endTime + " 23:59:59"
                );

        Page<CashRecharge> cashRechargePage = page(page, queryWrapper);
        List<CashRecharge> records = cashRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            if (CollectionUtils.isEmpty(basicUsers)) {
                List<Long> ids = records.stream().map(CashRecharge::getUserId).collect(Collectors.toList());
                basicUsers = userServiceFeign.getBasicUsers(ids, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashRecharge -> {
                UserDto userDto = finalBasicUsers.get(cashRecharge.getUserId());
                if (userDto != null) {
                    cashRecharge.setUsername(userDto.getUsername());
                    cashRecharge.setRealName(userDto.getRealName());
                }
            });
        }
        return cashRechargePage;
    }

    /**
     * 现金充值审核
     *
     * @param userId                  审核人
     * @param cashRechargeAuditRecord 审核数据
     * @return                        是否审核成功
     */
    @Override
    public boolean cashRechargeAudit(Long userId, CashRechargeAuditRecord cashRechargeAuditRecord) {
        // 当一个员工审核时，另一个员工不能再审核，需要加锁
        // key : CASH_RECHARGE_LOCK: + cashRechargeAuditRecord.getId()
        boolean tryLockAndRun = cache.tryLockAndRun(cashRechargeAuditRecord.getId() + "", 300, TimeUnit.SECONDS, () -> {
            // 审核
            Long recordId = cashRechargeAuditRecord.getId();
            CashRecharge cashRecharge = getById(recordId);
            if (cashRecharge == null) {
                throw new IllegalArgumentException("当前充值记录不存在");
            }
            Byte status = cashRecharge.getStatus();
            if (status == 1) {
                throw new IllegalArgumentException("已经审核通过了无需再次审核");
            }
            CashRechargeAuditRecord rechargeAuditRecord = new CashRechargeAuditRecord();
            rechargeAuditRecord.setAuditUserId(userId);
            rechargeAuditRecord.setStatus(cashRechargeAuditRecord.getStatus());
            rechargeAuditRecord.setRemark(cashRechargeAuditRecord.getRemark());
            int step = cashRecharge.getStep() + 1;
            rechargeAuditRecord.setStep((byte) step);

            // 保存审核记录
            int insert = cashRechargeAuditRecordMapper.insert(rechargeAuditRecord);
            if (insert <= 0) {
                throw new IllegalArgumentException("审核记录保存失败");
            }

            cashRecharge.setStatus(cashRechargeAuditRecord.getStatus());
            cashRecharge.setRemark(cashRechargeAuditRecord.getRemark());
            cashRecharge.setStep((byte) step);
            // 管理员通过审核 or 没有通过审核
            if (cashRechargeAuditRecord.getStatus() == 2) { // 拒绝
                updateById(cashRecharge);
            } else { // 审核通过，进行充值
                boolean transfer = accountService.transferAccount(userId, cashRecharge.getUserId(), cashRecharge.getCoinId(), cashRecharge.getId(), cashRecharge.getNum(), cashRecharge.getFee(), "充值", "recharge_into", (byte)1);
                if (transfer) {
                    cashRecharge.setLastTime(new Date()); // 设置完成时间
                    updateById(cashRecharge);
                }
            }
        });
        return tryLockAndRun;
    }

    /**
     * 查询当前用户的充值状态
     *
     * @param page
     * @param userId
     * @param status
     * @return
     */
    @Override
    public Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long userId, Byte status) {
        return page(page, new LambdaQueryWrapper<CashRecharge>().eq(CashRecharge::getUserId, userId).eq(status != null, CashRecharge::getStatus, status));
    }

    /**
     * 进行GCN购买的返回结果
     *
     * @param userId
     * @param cashParam
     * @return
     */
    @Override
    public CashTradeVo buy(Long userId, CashParam cashParam) {

        // 校验现金参数
        checkCashParam(cashParam);
        // 查询公司的银行卡
        List<AdminBankDto> allAdminBanks = adminBankServiceFeign.getAllAdminBanks();
        // 只需要一张银行卡就行
        AdminBankDto adminBankDto = loadBalance(allAdminBanks);
        // 生成订单号 参考号
        String nextId = String.valueOf(snowflake.nextId());
        String remark = RandomUtil.randomNumbers(6);
        // 在数据库插入一条充值记录
        CashRecharge cashRecharge = new CashRecharge();
        cashRecharge.setUserId(userId);
        assert adminBankDto != null;
        // 银行卡信息
        cashRecharge.setName(adminBankDto.getName());
        cashRecharge.setBankName(adminBankDto.getBankName());
        cashRecharge.setBankCard(adminBankDto.getBankCard());

        Coin coinServiceById = coinService.getById(cashParam.getCoinId());
        if (coinServiceById == null) {
            throw new IllegalArgumentException("id不存在");
        }

        Config buyCGNRate = configService.getConfigByCode("CNY2USDT");
        // 这里必须自己在数据库查取 前端不可靠
        BigDecimal realMum = cashParam.getMum().multiply(new BigDecimal(buyCGNRate.getValue())).setScale(2, RoundingMode.HALF_UP);

        cashRecharge.setTradeno(nextId);
        cashRecharge.setCoinId(cashParam.getCoinId());
        cashRecharge.setCoinName(coinServiceById.getName());
        cashRecharge.setNum(cashParam.getNum());
        cashRecharge.setMum(realMum);
        cashRecharge.setFee(BigDecimal.ZERO);
        cashRecharge.setType("linepay"); // 支付方式 ： 在线支付
        cashRecharge.setStatus((byte) 0); // 待审核
        cashRecharge.setStep((byte) 1); // 第一级别

        boolean save = save(cashRecharge);
        if (save) {
            // 返回
            CashTradeVo cashTradeVo = new CashTradeVo();
            // 收户行 信息
            cashTradeVo.setAmount(realMum);
            cashTradeVo.setStatus((byte) 0);
            cashTradeVo.setBankName(adminBankDto.getBankName());
            cashTradeVo.setBankCard(adminBankDto.getBankCard());
            cashTradeVo.setName(adminBankDto.getName());
            cashTradeVo.setRemark(remark);
            return cashTradeVo;
        }
        return null;
    }

    /**
     * 随机选择一个银行卡进行交易
     * @param allAdminBanks
     * @return
     */
    private AdminBankDto loadBalance(List<AdminBankDto> allAdminBanks) {
        if (CollectionUtils.isEmpty(allAdminBanks)) {
            throw new IllegalArgumentException("没有发现可用的银行卡");
        }
        if (allAdminBanks.size() == 1) {
            return allAdminBanks.get(0);
        }
        Random random = new Random();
        return allAdminBanks.get(random.nextInt(allAdminBanks.size()));
    }

    private void checkCashParam(CashParam cashParam) {
        @NotNull BigDecimal cashParamNum = cashParam.getNum(); // 现金充值数量
        Config cashParamNumCode = configService.getConfigByCode("WITH_DROW");
        @NotBlank String numCodeValue = cashParamNumCode.getValue();
        BigDecimal min = new BigDecimal(numCodeValue);
        if (cashParamNum.compareTo(min) >= 0) {
            throw new IllegalArgumentException("充值数量太小，小于最新充值数量");
        }
    }
}
