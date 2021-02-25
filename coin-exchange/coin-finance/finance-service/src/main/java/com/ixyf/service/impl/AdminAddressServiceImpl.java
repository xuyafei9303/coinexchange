package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Coin;
import com.ixyf.service.CoinService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.AdminAddress;
import com.ixyf.mapper.AdminAddressMapper;
import com.ixyf.service.AdminAddressService;

@Service
public class AdminAddressServiceImpl extends ServiceImpl<AdminAddressMapper, AdminAddress> implements AdminAddressService{

    @Resource
    private CoinService coinService;

    @Override
    public Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId) {
        return page(page, new LambdaQueryWrapper<AdminAddress>().eq(coinId != null, AdminAddress::getCoinId, coinId));
    }

    @Override
    public boolean save(AdminAddress entity) {
        @NotNull Long coinId = entity.getCoinId();
        Coin coin = coinService.getById(coinId);
        if (coin == null) {
            throw new IllegalArgumentException("输入的币种id错误");
        }
        String type = coin.getType();
        entity.setCoinType(type);

        return super.save(entity);
    }
}
