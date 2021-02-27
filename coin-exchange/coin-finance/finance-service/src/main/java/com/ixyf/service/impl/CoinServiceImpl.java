package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.Coin;
import com.ixyf.mapper.CoinMapper;
import com.ixyf.service.CoinService;
import org.springframework.util.StringUtils;

@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService{

    @Override
    public Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page) {
        return page(page,
                new LambdaQueryWrapper<Coin>()
                        .like(!StringUtils.isEmpty(name), Coin::getName, name)
                        .like(!StringUtils.isEmpty(type), Coin::getType, type)
                        .eq(status != null, Coin::getStatus, status)
                        .eq(!StringUtils.isEmpty(title), Coin::getTitle, title)
                        .eq(!StringUtils.isEmpty(walletType), Coin::getWallet, walletType)
        );
    }

    @Override
    public List<Coin> getCoinsByStatus(Byte status) {
        return list(new LambdaQueryWrapper<Coin>().eq(Coin::getStatus, status));
    }

    /**
     * 根据货币名称查询货币
     *
     * @param coinName
     * @return
     */
    @Override
    public Coin getCoinByCoinName(String coinName) {
        return getOne(new LambdaQueryWrapper<Coin>().eq(Coin::getName, coinName));
    }
}
