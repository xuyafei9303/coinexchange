package com.ixyf.service;

import com.ixyf.domain.CoinConfig;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinConfigService extends IService<CoinConfig>{

    /**
     * 通过币种的id查询币种的配置信息
     * @param coinId
     * @return
     */
    CoinConfig findByCoinId(Long coinId);

    /**
     * 新增or修改币种配置
     * @param coinConfig
     * @return
     */
    boolean updateOrSave(CoinConfig coinConfig);
}
