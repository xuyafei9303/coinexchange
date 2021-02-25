package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CoinType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinTypeService extends IService<CoinType>{


    /**
     * 分页展示币种类型
     * @param page
     * @param code 类型code
     * @return
     */
    Page<CoinType> findByPage(Page<CoinType> page, String code);

    /**
     * 根据币种类型的状态查询所有币种
     * @param status
     * @return
     */
    List<CoinType> findAllCoinTypesByStatus(Byte status);
}
