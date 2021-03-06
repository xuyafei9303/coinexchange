package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashWithdrawals;
import com.ixyf.domain.CoinRecharge;
import com.ixyf.model.R;
import com.ixyf.service.CoinRechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@Api(tags = "充币记录")
@RequestMapping(value = "/coinRecharges")
public class CoinRechargeController {

    @Resource
    private CoinRechargeService coinRechargeService;

    @GetMapping("/records")
    @ApiOperation(value = "充币记录分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "coinId", value = "币种id"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public R<Page<CoinRecharge>> findByPage(
            @ApiIgnore Page<CoinRecharge> page,
            Long coinId, Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax, String startTime, String endTime
    ) {
        Page<CoinRecharge> coinRechargePage = coinRechargeService.findByPage(page, coinId, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        return R.ok(coinRechargePage);
    }
}
