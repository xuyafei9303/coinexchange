package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AccountDetail;
import com.ixyf.model.R;
import com.ixyf.service.AccountDetailService;
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
@Api(tags = "资金流水")
@RequestMapping(value = "/accountDetails")
public class AccountDetailController {

    @Resource
    private AccountDetailService accountDetailService;

    @GetMapping("/records")
    @ApiOperation(value = "根据条件分页查询资金流水")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "coinId", value = "币种id"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "amountId", value = "账户id"),
            @ApiImplicitParam(name = "amountStart", value = "最小金额起始"),
            @ApiImplicitParam(name = "amountEnd", value = "最大金额截止"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public R<Page<AccountDetail>> findByPage(@ApiIgnore Page<AccountDetail> page,
                                             Long coinId, Long userId, String userName, String mobile, String amountId,
                                             String amountStart, String amountEnd, String startTime, String endTime
    ) {
        Page<AccountDetail> accountDetailPage = accountDetailService.findByPage(page, coinId, userId, userName, mobile,amountId, amountStart, amountEnd, startTime, endTime);
        return R.ok(accountDetailPage);
    }
}
