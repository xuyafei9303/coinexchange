package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.form.UpdateLoginPasswordForm;
import com.ixyf.form.UpdatePhoneForm;
import com.ixyf.form.UserAuthForm;

import java.util.List;

public interface UserService extends IService<User>{

    /**
     * 根据参数条件查询会员列表
     * @param page 分页参数
     * @param mobile 会员手机号
     * @param userId 会员id
     * @param userName 会员名
     * @param realName 会员真实姓名
     * @param status 会员状态
     * @param reviewsStatus 用户审核状态 这里是两个分页使用同一个接口 分别对应的是status和reviewsStatus
     * @return
     */
    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Integer reviewsStatus);

    /**
     * 通过用户的id查看该用户邀请的用户列表
     * @param page 分页参数
     * @param userId 用户id
     * @return
     */
    Page<User> findByDirectInvitesPage(Page<User> page, Long userId);

    /**
     * 修改用户的审核状态
     * @param id
     * @param authStatus
     * @param authCode
     * @param remark 审核被拒绝时的拒绝原因
     */
    void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark);

    /**
     * 用户的实名认证
     * @param id 用户id
     * @param userAuthForm 认证的表单请求
     * @return
     */
    boolean identifierVerify(Long id, UserAuthForm userAuthForm) throws Exception;

    /**
     * 用户高级认证
     * @param stringUser 用户
     * @param imgList 身份证图片数组
     */
    void authUser(Long stringUser, List<String> imgList);

    /**
     * 修改用户手机号
     * @param userId
     * @param updatePhoneForm
     * @return
     */
    boolean updatePhone(Long userId, UpdatePhoneForm updatePhoneForm);

    /**
     * 校验新手机号是否可用，若可用，则给新的手机号发送验证码
     * @param mobile 新的手机号
     * @param countryCode
     * @return
     */
    boolean checkNewPhone(String mobile, String countryCode);

    /**
     * 修改用户的登录密码
     * @param userId
     * @param updateLoginPasswordForm
     * @return
     */
    boolean updateLoginPassword(Long userId, UpdateLoginPasswordForm updateLoginPasswordForm);
}
