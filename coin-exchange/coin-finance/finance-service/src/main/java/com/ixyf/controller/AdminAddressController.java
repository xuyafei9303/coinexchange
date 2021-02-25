package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.AdminAddress;
import com.ixyf.model.R;
import com.ixyf.service.AdminAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@Api(tags = "钱包归集地址")
@RequestMapping("/adminAddress")
public class AdminAddressController {

    @Resource
    private AdminAddressService adminAddressService;

    @GetMapping
    @ApiOperation(value = "分页查询归集地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinId", value = "币种id"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数")
    })
    public R<Page<AdminAddress>> findPage(@ApiIgnore Page<AdminAddress> page, Long coinId) {
        Page<AdminAddress> addressPage = adminAddressService.findByPage(page, coinId);
        return R.ok(addressPage);
    }

    @PostMapping
    @ApiOperation(value = "新增归集地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminAddress", value = "adminAddress json")
    })
    public R save(@RequestBody @Validated AdminAddress adminAddress) {
        boolean save = adminAddressService.save(adminAddress);
        if (save) {
            return R.ok();
        }
        return R.fail("新增归集地址失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改归集地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminAddress", value = "adminAddress json")
    })
    public R update(@RequestBody @Validated AdminAddress adminAddress) {
        boolean update = adminAddressService.updateById(adminAddress);
        if (update) {
            return R.ok();
        }
        return R.fail("修改归集地址失败");
    }
}
