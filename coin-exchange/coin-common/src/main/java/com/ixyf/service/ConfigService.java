package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.domain.Config;

public interface ConfigService extends IService<Config>{

    /**
     *
     * @param page 分页参数
     * @param type 配置规则类型
     * @param code 配置规则代码
     * @param name 配置规则名称
     * @return
     */
    Page<Config> findByPage(Page<Config> page, String type, String code, String name);

    /**
     * 通过规则code查询签名
     * @param sign
     * @return
     */
    Config getConfigByCode(String sign);
}
