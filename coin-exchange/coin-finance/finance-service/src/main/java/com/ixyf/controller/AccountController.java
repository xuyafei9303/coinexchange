package com.ixyf.controller;

import com.ixyf.domain.Account;
import com.ixyf.model.R;
import com.ixyf.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(tags = "资产服务")
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @GetMapping("/{coinName}")
    @ApiOperation(value = "获取当前用户的货币资产情况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinName", value = "货币名称")
    })
    public R<Account> getUserAccount(@PathVariable("coinName") String coinName) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Account account = accountService.findByUserAndCoin(userId, coinName);
        return R.ok(account);
    }
}
