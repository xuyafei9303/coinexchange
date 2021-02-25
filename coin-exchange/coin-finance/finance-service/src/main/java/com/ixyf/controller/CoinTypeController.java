package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Coin;
import com.ixyf.domain.CoinType;
import com.ixyf.model.R;
import com.ixyf.service.CoinTypeService;
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
 * 权限：
 * trade_coin_type_query
 * trade_coin_type_create
 * trade_coin_type_update
 * trade_coin_type_delete
 */
@RestController
@Api(tags = "币种类型控制器")
@RequestMapping("/coinTypes")
public class CoinTypeController {

    @Resource
    private CoinTypeService coinTypeService;

    @GetMapping
    @ApiOperation(value = "条件分页币种类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "code", value = "币种类型")
    })
    @PreAuthorize("hasAuthority('trade_coin_type_query')")
    public R<Page<CoinType>> findByPage(@ApiIgnore Page<CoinType> page, String code) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<CoinType> coinTypePage = coinTypeService.findByPage(page, code);
        return R.ok(coinTypePage);
    }

    @PostMapping
    @ApiOperation(value = "新增币种")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinType", value = "coinType json")
    })
    @PreAuthorize("hasAuthority('trade_coin_type_create')")
    public R addCoinType(@RequestBody @Validated CoinType coinType) {
        boolean save = coinTypeService.save(coinType);
        if (save) {
            return R.ok();
        }
        return R.fail("新增币种类型失败");
    }

    @PatchMapping
    @ApiOperation(value = "编辑币种")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinType", value = "coinType json")
    })
    @PreAuthorize("hasAuthority('trade_coin_type_update')")
    public R updateCoinType(@RequestBody @Validated CoinType coinType) {
        boolean update = coinTypeService.updateById(coinType);
        if (update) {
            return R.ok();
        }
        return R.fail("编辑币种类型失败");
    }

    @PostMapping("/setStatus")
    @ApiOperation(value = "设置币种状态 启用or禁用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id"),
            @ApiImplicitParam(name = "status", value = "币种状态")
    })
    @PreAuthorize("hasAuthority('trade_coin_type_update')")
    public R setStatus(@RequestBody CoinType coinType) {
        boolean update = coinTypeService.updateById(coinType);
        if (update) {
            return R.ok();
        }
        return R.fail("设置币种状态失败");
    }
}
