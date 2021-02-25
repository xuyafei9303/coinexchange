package com.ixyf.controller;

import com.ixyf.domain.CoinConfig;
import com.ixyf.model.R;
import com.ixyf.service.CoinConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(tags = "币种配置")
@RequestMapping("/coinConfigs")
public class CoinConfigController {

    @Resource
    private CoinConfigService coinConfigService;

    @GetMapping("/info/{coinId}")
    @ApiOperation(value = "查询币种的配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinId", value = "币种id值")
    })
    public R<CoinConfig> getCoinConfig(@PathVariable("coinId") Long coinId) {
        CoinConfig coinConfig = coinConfigService.findByCoinId(coinId);
        return R.ok(coinConfig);
    }

    @PatchMapping
    @ApiOperation(value = "修改币种配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinConfig", value = "coinConfig json")
    })
    public R updateCoinConfig(@RequestBody @Validated CoinConfig coinConfig) {
        boolean updateOrSave = coinConfigService.updateOrSave(coinConfig);
        if (updateOrSave) {
            return R.ok();
        }
        return R.fail("修改币种配置失败");
    }
}
