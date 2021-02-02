package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.WebConfig;
import com.baomidou.mybatisplus.extension.service.IService;
public interface WebConfigService extends IService<WebConfig>{
    /**
     * 分页展示资源配置项
     * @param page 分页参数
     * @param name 配置名称
     * @param type 配置类型
     * @return
     */
    Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type);
}
