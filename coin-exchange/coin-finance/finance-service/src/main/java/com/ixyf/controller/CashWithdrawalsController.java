package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashRecharge;
import com.ixyf.domain.CashWithdrawals;
import com.ixyf.model.R;
import com.ixyf.service.CashWithdrawalsService;
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
@Api(tags = "提现记录")
@RequestMapping("/cashWithdrawals")
public class CashWithdrawalsController {

    @Resource
    private CashWithdrawalsService cashWithdrawalsService;

    @GetMapping("/records")
    @ApiOperation(value = "分页查询提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public R<Page<CashWithdrawals>> findByPage(@ApiIgnore Page<CashWithdrawals> page,
                                               Long userId, String userName, String mobile,
                                               Byte status, String numMin, String numMax, String startTime, String endTime

    ) {
        Page<CashWithdrawals> cashWithdrawalsPage = cashWithdrawalsService.findByPage(page, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        return R.ok(cashWithdrawalsPage);
    }
}
