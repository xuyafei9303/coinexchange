package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.UserAuthInfoMapper;
import com.ixyf.domain.UserAuthInfo;
import com.ixyf.service.UserAuthInfoService;
@Service
public class UserAuthInfoServiceImpl extends ServiceImpl<UserAuthInfoMapper, UserAuthInfo> implements UserAuthInfoService{

    @Override
    public List<UserAuthInfo> getUserAuthInfoByCode(Byte authCode) {
        return list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getAuthCode, authCode)); // 通过认证的唯一code来查询用户认证信息
    }

    @Override
    public List<UserAuthInfo> getUserAuthInfoByUserId(Long id) {
        List<UserAuthInfo> list = list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getUserId, id));
        return list == null ? Collections.emptyList() : list; // 处理null的情况
    }
}
