package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.User;
import com.ixyf.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.UserBankMapper;
import com.ixyf.domain.UserBank;
import com.ixyf.service.UserBankService;
@Service
public class UserBankServiceImpl extends ServiceImpl<UserBankMapper, UserBank> implements UserBankService{

    @Resource
    private UserService userService;

    @Override
    public boolean bindBank(Long userId, UserBank userBank) {
        // 判断支付密码
        String payPassword = userBank.getPayPassword();
        User user = userService.getById(userId);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(payPassword, user.getPaypassword())) {
            throw new IllegalArgumentException("用户的支付密码错误");
        }

        Long userBankId = userBank.getId();
        if (userBankId != null) { // 有id 修改操作
            UserBank bank = getById(userBankId);
            if (bank == null) {
                throw new IllegalArgumentException("用户银行卡的id输入错误");
            }
            return updateById(userBank); // 修改值
        }
        // 如果用户银行卡id为空，则需要新建一个
        userBank.setUserId(userId);

        return save(userBank);
    }

    @Override
    public UserBank getCurrentUserBank(Long userId) {
        return getOne(
                new LambdaQueryWrapper<UserBank>()
                        .eq(UserBank::getUserId, userId)
                        .eq(UserBank::getStatus, 1)
        );
    }

    @Override
    public Page<UserBank> findByPage(Page<UserBank> page, Long usrId) {
        return page(page, new LambdaQueryWrapper<UserBank>()
                .eq(usrId != null, UserBank::getUserId, usrId)
        );
    }
}
