package com.ixyf.controller;

import com.ixyf.domain.Coin;
import com.ixyf.domain.CoinConfig;
import com.ixyf.model.R;
import com.ixyf.service.CoinConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
