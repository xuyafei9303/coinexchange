package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserBank;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserBankService extends IService<UserBank>{


    /**
     * 分页显示用户银行卡信息
     * @param page 分页参数
     * @param usrId 用户id
     * @return
     */
    Page<UserBank> findByPage(Page<UserBank> page, Long usrId);

    /**
     * 根据用户id查询用户银行卡
     * @param userId
     * @return
     */
    UserBank getCurrentUserBank(Long userId);

    /**
     * 绑定银行卡
     * @param userId
     * @param userBank
     * @return
     */
    boolean bindBank(Long userId, UserBank userBank);
}
