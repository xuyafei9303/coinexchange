package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysUser;
import com.ixyf.model.R;
import com.ixyf.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

/**
 * sys_user_query
 * sys_user_delete
 * sys_user_create
 * sys_user_update
 */
@RestController
@Api(tags = "员工管理")
@RequestMapping("/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "mobile", value = "员工手机号"),
            @ApiImplicitParam(name = "fullname", value = "员工名")
    })
    @ApiOperation(value = "分页展示员工数据")
    @GetMapping
    @PreAuthorize("hasAuthority('sys_user_query')")
    public R<Page<SysUser>> findByPage(@ApiIgnore Page<SysUser> page, String mobile, String fullname) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<SysUser> sysUserPage = sysUserService.findByPage(page, mobile, fullname);
        return R.ok(sysUserPage);
    }

    @PostMapping
    @ApiOperation(value = "新增一个user")
    @PreAuthorize("hasAuthority('sys_user_create')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUser", value = "sysUser的json数据")
    })
    public R addUser(@RequestBody SysUser sysUser) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        sysUser.setCreateBy(Long.valueOf(userId));
        boolean add = sysUserService.addUser(sysUser);
        if (add) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @PostMapping("/delete")
    @ApiOperation("删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "删除用户的ids列表")
    })
    public R deleteUser(@RequestBody Long [] ids) {
        boolean delete = sysUserService.removeByIds(Arrays.asList(ids));
        if (delete) {
            return R.ok();
        }
        return R.fail("删除失败");
    }
}
