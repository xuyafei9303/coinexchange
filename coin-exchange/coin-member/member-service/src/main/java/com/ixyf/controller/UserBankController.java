package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserBank;
import com.ixyf.model.R;
import com.ixyf.service.UserBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@Api(tags = "用户银行卡管理")
@RequestMapping("/userBanks")
public class UserBankController {

    @Resource
    private UserBankService userBankService;

    @GetMapping
    @ApiOperation(value = "分页查询用户的银行卡信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "usrId", value = "会员id")
    })
    @PreAuthorize("hasAuthority('user_bank_query')")
    public R<Page<UserBank>> findByPage(@ApiIgnore Page<UserBank> page, Long usrId) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<UserBank> userBankPage = userBankService.findByPage(page, usrId);
        return R.ok(userBankPage);
    }

    @PostMapping("/status")
    @ApiOperation(value = "启用or禁用银行卡状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员银行卡id"),
            @ApiImplicitParam(name = "status", value = "银行卡状态")
    })
    public R changeStatus(Long id, Byte status) {
        UserBank userBank = new UserBank();
        userBank.setId(id);
        userBank.setStatus(status);
        boolean update = userBankService.updateById(userBank);
        if (update) {
            return R.ok("修改银行卡状态成功");
        }
        return R.fail("修改银行卡状态失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改银行卡信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userBank", value = "银行卡json数据")
    })
    public R update(@RequestBody @Validated UserBank userBank) {
        boolean update = userBankService.updateById(userBank);
        if (update) {
            return R.ok("修改银行卡信息成功");
        }
        return R.fail("修改银行卡信息失败");
    }

}
