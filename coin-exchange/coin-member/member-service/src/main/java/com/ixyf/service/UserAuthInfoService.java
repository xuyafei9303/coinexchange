package com.ixyf.service;

import com.ixyf.domain.UserAuthInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthInfoService extends IService<UserAuthInfo>{

    /**
     * 通过认证的code来查询用户的认证详情
     * @param authCode 认证的唯一code
     * @return
     */
    List<UserAuthInfo> getUserAuthInfoByCode(Byte authCode);

    /**
     * 用户未被认证，根据用户id来查询用户的认证列表
     * @param id
     * @return
     */
    List<UserAuthInfo> getUserAuthInfoByUserId(Long id);
}
