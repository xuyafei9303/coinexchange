package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Coin;
import com.ixyf.model.R;
import com.ixyf.service.CoinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "数字货币")
@RequestMapping("/coins")
public class CoinController {

    @Resource
    private CoinService coinService;

    @GetMapping
    @ApiOperation(value = "分页查询数字货币")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "币种名称"),
            @ApiImplicitParam(name = "type", value = "币种类型"),
            @ApiImplicitParam(name = "status", value = "币种状态"),
            @ApiImplicitParam(name = "title", value = "标题"),
            @ApiImplicitParam(name = "walletType", value = "钱包类型"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
    })
    public R<Page<Coin>> findByPage(
            String name,
            String type,
            Byte status,
            String title,
            @RequestParam(name = "wallet_type", required = false) String walletType,
            @ApiIgnore Page<Coin> page
    ) {
        Page<Coin> coinPage = coinService.findByPage(name, type, status, title, walletType, page);
        return R.ok(coinPage);
    }

    @PostMapping
    @ApiOperation(value = "启用 or 禁用 币种状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "coin json")
    })
    public R setStatus(@RequestBody Coin coin) {
        boolean update = coinService.updateById(coin);
        if (update) {
            return R.ok();
        }
        return R.fail("设置币种状态失败");
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "查询币种的详细信息")
    public R<Coin> info(@PathVariable("id") Long id) {
        Coin coin = coinService.getById(id);
        return R.ok(coin);
    }

    @GetMapping("/all")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "币种当前状态")
    })
    public R<List<Coin>> getCoinAll(Byte status) {
        List<Coin> coins = coinService.getCoinsByStatus(status);
        return R.ok(coins);
    }
}
