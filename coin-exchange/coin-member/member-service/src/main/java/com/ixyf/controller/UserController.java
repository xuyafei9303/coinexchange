package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.User;
import com.ixyf.model.R;
import com.ixyf.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

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
        Page<User> userPage = userService.findByPage(page, mobile, userId, userName, realName, status);
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
            return R.ok("更新会员信息成功");
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
}
