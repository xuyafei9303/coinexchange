package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.ConfigMapper;
import com.ixyf.domain.Config;
import com.ixyf.service.ConfigService;
import org.springframework.util.StringUtils;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService{

    @Override
    public Page<Config> findByPage(Page<Config> page, String type, String code, String name) {
        return page(page, new LambdaQueryWrapper<Config>()
                .like(!StringUtils.isEmpty(type), Config::getType, type)
                .like(!StringUtils.isEmpty(code), Config::getCode, code)
                .like(!StringUtils.isEmpty(name), Config::getName, name)
        );
    }
}
