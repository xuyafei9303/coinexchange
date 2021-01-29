package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysPrivilege;
import com.ixyf.model.R;
import com.ixyf.service.SysPrivilegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 权限：
 * 查询 sys_privilege_query
 * 新增 sys_privilege_create
 * 修改 sys_privilege_update
 * 删除 sys_privilege_delete
 */
@RestController
@RequestMapping("/privileges")
@Api(tags = "权限管理控制器")
public class  SysPrivilegeController {

    @Resource
    private SysPrivilegeService sysPrivilegeService;

    /**
     * 数据分页查询
     * @param page
     * @return
     */
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示大小")
    })
    @PreAuthorize("hasAuthority('sys_privilege_query')")
    public R<Page<SysPrivilege>> findByPage(@ApiIgnore Page<SysPrivilege> page) {
        // 查询时，要将最近新增或者修改的数据优先展示出来
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<SysPrivilege> privilegePage = sysPrivilegeService.page(page);
        return R.ok(privilegePage);
    }

    @PostMapping
    @ApiOperation(value = "新增权限")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sysPrivilege", value = "sysPrivilege的json数据")
    })
    @PreAuthorize("hasAuthority('sys_privilege_create')")
    public R add(@RequestBody SysPrivilege sysPrivilege) {
        // 新增时需要给新增的对象添加一些属性
//        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        sysPrivilege.setCreateBy(Long.valueOf(userIdStr));
//        sysPrivilege.setCreated(new Date());
//        sysPrivilege.setLastUpdateTime(new Date());
        boolean save = sysPrivilegeService.save(sysPrivilege);
        if (save) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysPrivilege", value = "sysPrivilege的json数据")
    })
    @PreAuthorize("hasAuthority('sys_privilege_update')")
    public R update(@RequestBody SysPrivilege sysPrivilege) {
        // 新增时需要给新增的对象添加一些属性
//        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        sysPrivilege.setModifyBy(Long.valueOf(userIdStr));
//        sysPrivilege.setLastUpdateTime(new Date());
        boolean update = sysPrivilegeService.updateById(sysPrivilege);
        if (update) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }
}
