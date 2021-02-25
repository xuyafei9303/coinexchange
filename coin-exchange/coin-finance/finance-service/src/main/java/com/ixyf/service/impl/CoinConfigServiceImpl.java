package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ixyf.domain.Coin;
import com.ixyf.service.CoinService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CoinConfig;
import com.ixyf.mapper.CoinConfigMapper;
import com.ixyf.service.CoinConfigService;
@Service
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigMapper, CoinConfig> implements CoinConfigService{

    @Resource
    private CoinService coinService;

    @Override
    public CoinConfig findByCoinId(Long coinId) {
        return getOne(new LambdaQueryWrapper<CoinConfig>().eq(CoinConfig::getId, coinId));
    }

    @Override
    public boolean updateOrSave(CoinConfig coinConfig) {

        Coin coin = coinService.getById(coinConfig.getId());
        if (coin == null) {
            throw new IllegalArgumentException("coinId不存在");
        }
        coinConfig.setCoinType(coin.getType());
        coinConfig.setName(coin.getName());

        CoinConfig config = getById(coinConfig.getId());
        if (config == null) {
            // 新增操作
            return save(coinConfig);
        } else {
            // 修改操作
            return updateById(coinConfig);
        }
    }
}
