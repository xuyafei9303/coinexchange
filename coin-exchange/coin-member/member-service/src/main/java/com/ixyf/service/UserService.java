package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserService extends IService<User>{

    /**
     * 根据参数条件查询会员列表
     * @param page 分页参数
     * @param mobile 会员手机号
     * @param userId 会员id
     * @param userName 会员名
     * @param realName 会员真实姓名
     * @param status 会员状态
     * @return
     */
    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status);

    /**
     * 通过用户的id查看该用户邀请的用户列表
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */
    Page<User> findByDirectInvitesPage(Page<User> page, Long userId);
}
