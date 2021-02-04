package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserAuthAuditRecord;
import com.ixyf.service.UserAuthAuditRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.User;
import com.ixyf.mapper.UserMapper;
import com.ixyf.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserAuthAuditRecordService userAuthAuditRecordService;

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
}
