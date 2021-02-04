package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.User;
import com.ixyf.domain.UserAuthAuditRecord;
import com.ixyf.domain.UserAuthInfo;
import com.ixyf.model.R;
import com.ixyf.service.UserAuthAuditRecordService;
import com.ixyf.service.UserAuthInfoService;
import com.ixyf.service.UserService;
import com.ixyf.vo.AuthUserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * user_query
 * user_update
 * user_export
 */
@RestController
@Api(tags = "会员中心")
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserAuthAuditRecordService auditRecordService;

    @Resource
    private UserAuthInfoService userAuthInfoService;

    @GetMapping
    @ApiOperation(value = "根据条件分页查询用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "mobile", value = "会员手机号"),
            @ApiImplicitParam(name = "userId", value = "会员id"),
            @ApiImplicitParam(name = "userName", value = "会员名"),
            @ApiImplicitParam(name = "realName", value = "会员真实姓名"),
            @ApiImplicitParam(name = "status", value = "会员状态")
    })
    @PreAuthorize("hasAuthority('user_query')")
    public R<Page<User>> findByPage(
            @ApiIgnore Page<User> page,
            String mobile,
            Long userId,
            String userName,
            String realName,
            Integer status) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<User> userPage = userService.findByPage(page, mobile, userId, userName, realName, status, null);
        return R.ok(userPage);
    }

    @PostMapping("/status")
    @ApiOperation(value = "修改会员状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员id"),
            @ApiImplicitParam(name = "status", value = "要修改的状态")
    })
    public R changeStatus(Long id, Byte status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean update = userService.updateById(user);
        if (update) {
            return R.ok("更新状态成功");
        }
        return R.fail("更新状态失败");
    }

    @PatchMapping
    @ApiOperation(value = "更新会员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "会员user的json数据")
    })
    @PreAuthorize("hasAuthority('user_update')")
    public R update(@RequestBody @Validated User user) {
        boolean update = userService.updateById(user);
        if (update) {
            return R.ok("更新会员" +
                    "信息成功");
        }
        return R.fail("更新会员信息失败");
    }

    @GetMapping("/info")
    @ApiOperation(value = "查看会员详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员id")
    })
    public R<User> userInfo(Long id) {
        User user = userService.getById(id);
        return R.ok(user);
    }

    @GetMapping("/directInvites")
    @ApiOperation(value = "查看该用户邀请的用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数")
    })
    public R<Page<User>> directInvites(@ApiIgnore Page<User> page, Long userId) {

        Page<User> userPage = userService.findByDirectInvitesPage(page, userId);
        return R.ok(userPage);
    }

    @GetMapping("/auths")
    @ApiOperation(value = "用户高级实名认证审核分页展示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "realName", value = "用户姓名"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "reviewsStatus", value = "审核状态,1通过,2拒绝,0,待审核"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数")
    })
    @PreAuthorize("hasAuthority('user_auth_query')")
    public R<Page<User>> authUserPage(Page<User> page, String realName,Long userId, String userName, String mobile, Integer reviewsStatus) {
        Page<User> userPage = userService.findByPage(page, realName, userId, userName, mobile, null, reviewsStatus);
        return R.ok(userPage);
    }

    /**
     * 详情包含三个部分：user(用户id，姓名等信息)，userAuthInfoList(用户审核证件照)，userAuditRecordList(用户审核历史)
     * @param id
     * @return
     */
    @GetMapping("/auth/info")
    @ApiOperation(value = "查询用户的认证详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id")
    })
    public R<AuthUserInfoVo> getAuthUserInfo(Long id) {
        User user = userService.getById(id);
        List<UserAuthAuditRecord> userAuthAuditRecords = null;
        List<UserAuthInfo> userAuthInfos = null;
        if (user != null) {
            // 用户的审核记录
            Integer reviewsStatus = user.getReviewsStatus();
            if (reviewsStatus == null || reviewsStatus == 0) { // 待审核
                userAuthAuditRecords = Collections.emptyList();
                userAuthInfos = userAuthInfoService.getUserAuthInfoByUserId(id);
            } else {
                userAuthAuditRecords = auditRecordService.getUserAuthAuditRecordList(id);
                // 查询用户的认证详情列表 -> 用户的身份信息
                UserAuthAuditRecord auditRecord = userAuthAuditRecords.get(0); // 之前是按照认证日期进行排序的，第0个值就是最近被认证的值
                Byte authCode = auditRecord.getStatus(); // 认证的唯一标识
                userAuthInfos = userAuthInfoService.getUserAuthInfoByCode(authCode);
            }
        }
        return R.ok(new AuthUserInfoVo(user,userAuthInfos, userAuthAuditRecords));
    }

    /**
     * 对身份证图片进行认证，符合条件则认证通过
     * @return
     */
    @PostMapping("/auths/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id"),
            @ApiImplicitParam(name = "authStatus", value = "用户审核状态"),
            @ApiImplicitParam(name = "authCode", value = "一组(身份证)图片的唯一标识"),
            @ApiImplicitParam(name = "remark", value = "审核被拒绝时填写的备注信息")
    })
    public R updateUserAuthStatus(@RequestParam(value = "id", required = true) Long id,
                                  @RequestParam(value = "authStatus", required = true) Byte authStatus,
                                  @RequestParam(value = "authCode", required = true) Long authCode,
                                  @RequestParam(value = "remark", required = true) String remark) {

        // 修改user里面的reviewStatus
        // 在authAuditRecord里面添加一条记录
        userService.updateUserAuthStatus(id, authStatus, authCode, remark);
        return R.ok();
    }
}
