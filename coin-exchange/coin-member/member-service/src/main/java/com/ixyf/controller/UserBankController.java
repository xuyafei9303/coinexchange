package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserBank;
import com.ixyf.dto.UserBankDto;
import com.ixyf.feign.UserBankServiceFeign;
import com.ixyf.mappers.UserBankDtoMapper;
import com.ixyf.model.R;
import com.ixyf.service.UserBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@Api(tags = "用户银行卡管理")
@RequestMapping("/userBanks")
public class UserBankController implements UserBankServiceFeign {

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

    @GetMapping("/current")
    @ApiOperation(value = "查询当前用户的银行卡")
    public R<UserBank> getCurrentUserBank() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserBank userBank = userBankService.getCurrentUserBank(userId);
        return R.ok(userBank);
    }

    @PostMapping("/bind")
    @ApiOperation(value = "绑定银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userBank", value = "userBank json")
    })
    public R bindBank(@RequestBody @Validated UserBank userBank) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean bind = userBankService.bindBank(userId, userBank);
        if (bind) {
            return R.ok();
        }
        return R.fail("绑定银行卡失败");
    }

    @Override
    public UserBankDto getUserBankInfo(Long userId) {
        UserBank currentUserBank = userBankService.getCurrentUserBank(userId);
        UserBankDto userBankDto = UserBankDtoMapper.INSTANCE.toConvertDto(currentUserBank);
        return userBankDto;
    }
}
