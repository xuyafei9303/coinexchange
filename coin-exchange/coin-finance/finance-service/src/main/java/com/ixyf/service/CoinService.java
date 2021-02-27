package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Coin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinService extends IService<Coin>{

    /**
     * 数字货币根据条件分页查询
     * @param name
     * @param type
     * @param status
     * @param title
     * @param walletType
     * @param page
     * @return
     */
    Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page);

    /**
     * 根据币种当前状态查询所有币种信息
     * @param status
     * @return
     */
    List<Coin> getCoinsByStatus(Byte status);

    /**
     * 根据货币名称查询货币
     * @param coinName 货币名称是唯一的
     * @return
     */
    Coin getCoinByCoinName(String coinName);
}
