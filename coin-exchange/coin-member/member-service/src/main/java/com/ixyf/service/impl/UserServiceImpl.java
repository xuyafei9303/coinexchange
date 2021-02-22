package com.ixyf.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.config.IDGenConfig;
import com.ixyf.config.IdenAuthenticationConfiguration;
import com.ixyf.domain.UserAuthAuditRecord;
import com.ixyf.domain.UserAuthInfo;
import com.ixyf.form.UserAuthForm;
import com.ixyf.geetest.GeetestLib;
import com.ixyf.service.UserAuthAuditRecordService;
import com.ixyf.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.User;
import com.ixyf.mapper.UserMapper;
import com.ixyf.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserAuthAuditRecordService userAuthAuditRecordService;

    @Resource
    private UserAuthInfoService userAuthInfoService;

    private final Snowflake snowflake = new Snowflake(IDGenConfig.appCode, IDGenConfig.machineCode);

    @Resource
    private GeetestLib geetestLib;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Integer reviewsStatus) {
        return page(page, new LambdaQueryWrapper<User>()
                .like(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
                .like(!StringUtils.isEmpty(userName), User::getUsername, userName)
                .like(!StringUtils.isEmpty(realName), User::getRealName, realName)
                .eq(userId != null, User::getId, userId)
                .eq(status != null, User::getStatus, status)
                .eq(reviewsStatus != null, User::getReviewsStatus, reviewsStatus)
        );
    }

    @Override
    public Page<User> findByDirectInvitesPage(Page<User> page, Long userId) {
        return page(page, new LambdaQueryWrapper<User>().eq(User::getDirectInviteid, userId));
    }

    @Override
    @Transactional
    public void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark) {
        log.info("开始修改用户的审核状态，当前用户：{}, 用户的审核状态：{}, 图片的唯一code：{}", id, authStatus, authCode);
        User user = getById(id);
        if (user != null) {
            user.setReviewsStatus(authStatus.intValue()); // 审核状态
            updateById(user); // 修改用户的状态
        }
        UserAuthAuditRecord userAuthAuditRecord = new UserAuthAuditRecord();
        userAuthAuditRecord.setUserId(id);
        userAuthAuditRecord.setStatus(authStatus);
        userAuthAuditRecord.setAuthCode(authCode);

        String userStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAuthAuditRecord.setAuditUserId(Long.valueOf(userStr));
        userAuthAuditRecord.setAuditUserName("xuyafei"); // 远程调用admin-service来获取
        userAuthAuditRecord.setRemark(remark);// 拒绝时填写

        userAuthAuditRecordService.save(userAuthAuditRecord);
    }

    @Override
    public boolean identifierVerify(Long id, UserAuthForm userAuthForm) throws Exception {
        User user = getById(id);
        Assert.notNull(user, "认证用户不存在");
        Byte authStatus = user.getAuthStatus();
        if (!authStatus.equals((byte) 0)) {
            throw new IllegalArgumentException("不合法的参数异常，该用户已经认证通过了");
        }
        // 执行认证
        checkForm(userAuthForm); // 极验
        // 实名认证
        boolean check = IdenAuthenticationConfiguration.check(userAuthForm.getRealName(), userAuthForm.getIdCard());
        if (!check) {
            throw new IllegalArgumentException("该用户信息错误");
        }
        user.setAuthtime(new Date());
        user.setAuthStatus((byte) 1);
        user.setRealName(userAuthForm.getRealName());
        user.setIdCard(userAuthForm.getIdCard());
        user.setIdCardType(userAuthForm.getIdCardType());

        return updateById(user);
    }

    /**
     * 用户高级认证
     * @param stringUser 用户
     * @param imgList 身份证图片数组
     */
    @Override
    @Transactional
    public void authUser(Long stringUser, List<String> imgList) {

        if (CollectionUtils.isEmpty(imgList)) throw new IllegalArgumentException("用户的身份证信息为空");

        User user = getById(stringUser);
        long authCode = snowflake.nextId(); // 使用雪花算法生成code
        List<UserAuthInfo> userAuthInfoList = new ArrayList<>(imgList.size());
        if (user == null) throw new IllegalArgumentException("用户不正确");
        for (int i = 0; i < imgList.size(); i++) {
            UserAuthInfo userAuthInfo = new UserAuthInfo();
            userAuthInfo.setImageUrl(imgList.get(i));
            userAuthInfo.setUserId(stringUser);
            userAuthInfo.setSerialno(i + 1); // 设置序号 按照顺序排列的 1正面 2反面 3手持
            userAuthInfo.setAuthCode(authCode); // 一组身份信息的标识 3个图片为一组
            userAuthInfoList.add(userAuthInfo);
        }
        userAuthInfoService.saveBatch(userAuthInfoList); // 批量插入
        user.setReviewsStatus(0); // 等待审核
        updateById(user); // 更新用户状态
    }

    private void checkForm(UserAuthForm userAuthForm) {
        userAuthForm.check(userAuthForm, geetestLib, redisTemplate);
    }

    @Override
    public User getById(Serializable id) {
        User user = super.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户id不正确");
        }
        Byte seniorAuthStatus = null;
        String seniorAuthDesc = "";
        Integer reviewsStatus = user.getReviewsStatus(); // 用户被审核的状态 审核状态,1通过,2拒绝,0,待审核
        if (reviewsStatus == null) {
            seniorAuthStatus = 3;
            seniorAuthDesc = "资料未填写";
        } else {
            switch (reviewsStatus) {
                case 1: // 通过
                    seniorAuthStatus = 1;
                    seniorAuthDesc = "审核通过";
                    break;
                case 2: // 拒绝
                    seniorAuthStatus = 2;
                    // 查询被拒绝的原因 -> 审核记录
                    // 按时间排序的 最近的一条
                    List<UserAuthAuditRecord> authAuditRecordList = userAuthAuditRecordService.getUserAuthAuditRecordList(user.getId());
                    if (!CollectionUtils.isEmpty(authAuditRecordList)) {
                        UserAuthAuditRecord authAuditRecord = authAuditRecordList.get(0);
                        seniorAuthDesc = authAuditRecord.getRemark();
                    }
                    break;
                case 0:
                    seniorAuthStatus = 0;
                    seniorAuthDesc = "等待审核";
                    break;
            }
        }
        user.setSeniorAuthStatus(seniorAuthStatus);
        user.setSeniorAuthDesc(seniorAuthDesc);
        return user;
    }
}
