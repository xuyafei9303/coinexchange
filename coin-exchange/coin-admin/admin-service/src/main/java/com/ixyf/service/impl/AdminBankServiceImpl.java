package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.dto.AdminBankDto;
import com.ixyf.mappers.AdminBankDtoMappers;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.AdminBank;
import com.ixyf.mapper.AdminBankMapper;
import com.ixyf.service.AdminBankService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank> implements AdminBankService{

    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        return page(page, new LambdaQueryWrapper<AdminBank>().like(!StringUtils.isEmpty(bankCard), AdminBank::getBankCard, bankCard));
    }

    /**
     * 查询所有的银行卡信息
     *
     * @return
     */
    @Override
    public List<AdminBankDto> getAllAdminBanks() {
        List<AdminBank> adminBanks = list(new LambdaQueryWrapper<AdminBank>().eq(AdminBank::getStatus, 1));
        if (CollectionUtils.isEmpty(adminBanks)) {
            return Collections.emptyList();
        }
        List<AdminBankDto> adminBankDtos = AdminBankDtoMappers.INSTANCE.toConvertDto(adminBanks);
        return adminBankDtos;
    }
}
