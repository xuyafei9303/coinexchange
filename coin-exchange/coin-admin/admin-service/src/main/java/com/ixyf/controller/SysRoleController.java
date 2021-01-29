package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysRole;
import com.ixyf.model.R;
import com.ixyf.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

/**
 * 角色管理
 *
 * 权限：
 * 新增： sys_role_create
 * 删除： sys_role_delete
 * 修改： sys_role_update
 * 查询： sys_role_query
 */
@RestController
@RequestMapping("/roles")
@Api(tags = "角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping
    @ApiOperation(value = "分页展示角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "name", value = "角色名称")
    })
    @PreAuthorize("hasAuthority('sys_role_query')")
    public R<Page<SysRole>> findByPage(@ApiIgnore Page<SysRole> page, String name) {
        page.addOrder(OrderItem.desc("last_update_time"));
        return R.ok(sysRoleService.findByPage(page, name));
    }

    @PostMapping
    @ApiOperation(value = "新增角色")
    @PreAuthorize("hasAuthority('sys_role_create')")
    public R add(@RequestBody @Validated SysRole sysRole) {
        boolean save = sysRoleService.save(sysRole);
        if (save) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "删除时传入的角色id集合")
    })
    @PreAuthorize("hasAuthority('sys_role_delete')")
    public R delete(@RequestBody String [] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选择要删除的角色");
        }
        boolean removeByIds = sysRoleService.removeByIds(Arrays.asList(ids));
        if (removeByIds) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
