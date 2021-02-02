package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AdminBank;
import com.ixyf.model.R;
import com.ixyf.service.AdminBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * admin_bank_query
 * admin_bank_delete
 * admin_bank_create
 * admin_bank_update
 */
@RestController
@Api(tags = "公司银行卡管理")
@RequestMapping("/adminBanks")
public class AdminBankController {

    @Resource
    private AdminBankService adminBankService;

    @GetMapping
    @ApiOperation(value = "银行卡分页展示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "bankCard", value = "银行卡卡号")
    })
    @PreAuthorize("hasAuthority('admin_bank_query')")
    public R<Page<AdminBank>> findByPage(@ApiIgnore Page<AdminBank> page, String bankCard) {
        Page<AdminBank> adminBankPage = adminBankService.findByPage(page, bankCard);
        return R.ok(adminBankPage);
    }

    @PostMapping
    @ApiOperation(value = "新增银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminBank", value = "adminBank的json数据")
    })
    @PreAuthorize("hasAuthority('admin_bank_create')")
    public R add(@RequestBody @Validated AdminBank adminBank) {
        boolean save = adminBankService.save(adminBank);
        if (save) {
            return R.ok();
        }
        return R.fail("新增银行卡失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminBank", value = "adminBank的json数据")
    })
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R update(@RequestBody @Validated AdminBank adminBank) {
        boolean update = adminBankService.updateById(adminBank);
        if (update) {
            return R.ok();
        }
        return R.fail("修改银行卡失败");
    }

    @PostMapping("/adminUpdateBankStatus")
    @ApiOperation(value = "启动or禁用银行卡状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankId", value = "银行卡id"),
            @ApiImplicitParam(name = "status", value = "银行卡状态")
    })
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R changeStatus(Long bankId, Byte status) {
        AdminBank adminBank = new AdminBank();
        adminBank.setId(bankId);
        adminBank.setStatus(status);
        boolean update = adminBankService.updateById(adminBank);
        if (update) {
            return R.ok();
        }
        return R.fail("状态修改失败");
    }
}
