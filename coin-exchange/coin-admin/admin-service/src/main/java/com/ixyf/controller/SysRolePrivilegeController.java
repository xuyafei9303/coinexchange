package com.ixyf.controller;

import com.ixyf.domain.SysMenu;
import com.ixyf.model.R;
import com.ixyf.model.RolePrivilegesParam;
import com.ixyf.service.SysRolePrivilegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色权限的配置")
@RestController
public class SysRolePrivilegeController {

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    @GetMapping("/roles_privileges")
    @ApiOperation(value = "查询角色的权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id")
    })
    public R<List<SysMenu>> findSysMenuAndPrivileges(Long roleId) {
        List<SysMenu> sysMenus = sysRolePrivilegeService.findSysMenuAndPrivileges(roleId);
        return R.ok(sysMenus);
    }

    @PostMapping("/grant_privileges")
    @ApiOperation("授予某种权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "privilegesParam", value = "角色拥有的权限列表json数据")
    })
    public R grantPrivileges(@RequestBody RolePrivilegesParam privilegesParam) {
        boolean isOk = sysRolePrivilegeService.grantPrivileges(privilegesParam);
        if (isOk) {
            return R.ok();
        }
        return R.fail("授予权限失败");
    }
}
