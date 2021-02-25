package com.ixyf.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.config.IDGenConfig;
import com.ixyf.config.IdenAuthenticationConfiguration;
import com.ixyf.domain.Sms;
import com.ixyf.domain.UserAuthAuditRecord;
import com.ixyf.domain.UserAuthInfo;
import com.ixyf.dto.UserDto;
import com.ixyf.form.*;
import com.ixyf.geetest.GeetestLib;
import com.ixyf.mappers.UserDtoMapper;
import com.ixyf.service.SmsService;
import com.ixyf.service.UserAuthAuditRecordService;
import com.ixyf.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private SmsService coinSmsService; // 狗日的这里和阿里云sdk里面的重名了一直报错

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

    @Override
    public boolean checkNewPhone(String mobile, String countryCode) {
        // 新手机号，没有被占用
        int count = count(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile).eq(User::getCountryCode, countryCode));
        if (count > 0) { // 已被占用
            throw new IllegalArgumentException("该手机号已经被占用");
        }
        // 向新的手机发送短信
        Sms sms = new Sms();
        sms.setMobile(mobile);
        sms.setCountryCode(countryCode);
        sms.setTemplateCode("CHANGE_PHONE_VERIFY"); // 模板代码 CHANGE_PHONE_VERIFY
        return coinSmsService.sendSms(sms);
    }

    @Override
    public boolean reSetLoginPassword(ResetPasswordForm resetPasswordForm) {
        log.info("开始重置登录密码 {}", JSON.toJSONString(resetPasswordForm, true));
        // 极验校验
        resetPasswordForm.check(geetestLib, redisTemplate);
        // 手机号码校验
        String phoneValidateCode = (String) redisTemplate.opsForValue().get("SMS:FORGOT_VERIFY:" + resetPasswordForm.getMobile());
        if (!resetPasswordForm.getValidateCode().equals(phoneValidateCode)) {
            throw new IllegalArgumentException("验证码错误");
        }
        // 数据库数据校验
        @NotBlank String mobile = resetPasswordForm.getMobile();
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user == null) {
            throw new IllegalArgumentException(("手机号错误 该用户不存在"));
        }
        user.setPassword(new BCryptPasswordEncoder().encode(resetPasswordForm.getPassword()));
        return updateById(user);
    }

    @Override
    public boolean register(RegisterForm registerForm) {
        log.info("用户开始注册: registerForm = {}", JSON.toJSONString(registerForm, true));
        @NotBlank String mobile = registerForm.getMobile();
        @NotBlank String email = registerForm.getEmail();
        // 简单校验
        if (StringUtils.isEmpty(mobile) && StringUtils.isEmpty(email)) {
            throw new IllegalArgumentException("手机号或邮箱不能同时为空");
        }
        int count = count(new LambdaQueryWrapper<User>()
                .eq(!StringUtils.isEmpty(email), User::getEmail, email)
                .eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (count > 0) {
            throw new IllegalArgumentException("手机号或者邮箱已经被注册");
        }

        registerForm.check(geetestLib, redisTemplate); // 极验验证
        User user = getUser(registerForm); // 构建新用户

        return save(user);
    }

    private User getUser(RegisterForm registerForm) {
        // 注册
        User user = new User();
        user.setEmail(registerForm.getEmail());
        user.setMobile(registerForm.getMobile());
        String encode = new BCryptPasswordEncoder().encode(registerForm.getPassword());
        user.setPassword(encode);
        user.setPaypassSetting(false);
        user.setStatus((byte)1);
        user.setType((byte)1);
        user.setAuthStatus((byte)0);
        user.setLogins(0);
        user.setInviteCode(RandomUtil.randomString(8)); // 用户邀请码
        if (!StringUtils.isEmpty(registerForm.getInvitionCode())) {
            User userPre = getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, registerForm.getInvitionCode()));
            if (userPre != null) {
                user.setDirectInviteid(String.valueOf(userPre.getId())); // 邀请人id
                user.setInviteRelation(String.valueOf(userPre.getId())); // 邀请人关系
            }
        }
        return user;
    }

    /**
     * 通过用户的信息查询用户
     * @param ids 远程调用时批量获取
     * @param userName 使用用户名查询一系列用户的集合
     * @param mobile 使用手机号码查询一系列用户的集合
     * @return
     */
    @Override
    public Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile) {
        if (CollectionUtils.isEmpty(ids) && StringUtils.isEmpty(userName) && StringUtils.isEmpty(mobile)) {
            return Collections.emptyMap();
        }
        List<User> list = list(new LambdaQueryWrapper<User>()
                .in(User::getId, ids)
                .like(!StringUtils.isEmpty(userName), User::getUsername, userName)
                .like(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        // 将user转换成userDto
        List<UserDto> userDtoList = UserDtoMapper.INSTANCE.convert2Dto(list);
        Map<Long, UserDto> userDtoMap = userDtoList.stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        return userDtoMap;
    }

    @Override
    public List<User> getUserInvites(Long userId) {
        List<User> users = list(new LambdaQueryWrapper<User>().eq(User::getDirectInviteid, userId));
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        users.forEach(user -> {
            user.setPaypassword("******");
            user.setPassword("******");
            user.setAccessKeyId("******");
            user.setAccessKeySecret("******");
        });
        return users;
    }

    @Override
    public boolean resetPayPassword(Long userId, ResetPayPasswordForm resetPayPasswordForm) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户id为空");
        }
        @NotBlank String validateCode = resetPayPasswordForm.getValidateCode();
        String verifyCode = (String) redisTemplate.opsForValue().get("SMS:FORGOT_PAY_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(verifyCode)) {
            throw new IllegalArgumentException("验证码错误");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPaypassword(passwordEncoder.encode(resetPayPasswordForm.getPayPassword()));

        return updateById(user);
    }

    @Override
    public boolean updatePayPassword(Long userId, UpdatePayPasswordForm updatePayPasswordForm) {
        // 查询用户
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户id不存在");
        }
        @NotBlank String oldPassword = updatePayPasswordForm.getOldpassword();
        // 校验旧密码 数据库里都是加密过后的密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(updatePayPasswordForm.getOldpassword(), user.getPaypassword());
        if (!matches) {
            throw new IllegalArgumentException("用户的原始密码错误");
        }
        // 校验手机验证码
        @NotBlank String validateCode = updatePayPasswordForm.getValidateCode();
        String keyCode = (String) redisTemplate.opsForValue().get("SMS:CHANGE_PAY_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(keyCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        user.setPaypassword(passwordEncoder.encode(updatePayPasswordForm.getNewpassword()));

        return updateById(user);
    }

    @Override
    public boolean updateLoginPassword(Long userId, UpdateLoginPasswordForm updateLoginPasswordForm) {
        // 查询用户
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户id不存在");
        }
        @NotBlank String oldPassword = updateLoginPasswordForm.getOldpassword();
        // 校验旧密码 数据库里都是加密过后的密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(updateLoginPasswordForm.getOldpassword(), user.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("用户的原始密码错误");
        }
        // 校验手机验证码
        @NotBlank String validateCode = updateLoginPasswordForm.getValidateCode();
        String keyCode = (String) redisTemplate.opsForValue().get("SMS:CHANGE_LOGIN_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(keyCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        user.setPassword(passwordEncoder.encode(updateLoginPasswordForm.getNewpassword()));

        return updateById(user);
    }

    @Override
    public boolean updatePhone(Long userId, UpdatePhoneForm updatePhoneForm) {
        // 查询用户
        User user = getById(userId);
        @NotBlank String oldMobile = user.getMobile();
        String oldMobileCode = (String) redisTemplate.opsForValue().get("SMS:VERIFY_OLD_PHONE:" + oldMobile);
        if (!updatePhoneForm.getOldValidateCode().equals(oldMobileCode)) {
            throw new IllegalArgumentException("旧手机的验证码错误");
        }
        String newMobileCode = (String) redisTemplate.opsForValue().get("SMS:VERIFY_OLD_PHONE:" + updatePhoneForm.getNewMobilePhone());
        if (!updatePhoneForm.getValidateCode().equals(newMobileCode)) {
            throw new IllegalArgumentException("新手机的验证码错误");
        }
        user.setMobile(updatePhoneForm.getNewMobilePhone());

        return updateById(user);
    }


    private void checkForm(UserAuthForm userAuthForm) {
        userAuthForm.check(geetestLib, redisTemplate);
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


    // TODO ENCODE
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("XYF701030x");
        System.out.println("encode = " + encode);
    }
}
