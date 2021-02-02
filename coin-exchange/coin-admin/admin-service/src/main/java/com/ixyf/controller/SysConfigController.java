package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Config;
import com.ixyf.model.R;
import com.ixyf.service.ConfigService;
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
 * config_query
 * config_update
 * config_delete
 * config_create
 */
@RestController
@Api(tags = "系统后台参数配置")
@RequestMapping("/configs")
public class SysConfigController {

    @Resource
    private ConfigService configService;

    @GetMapping
    @ApiOperation(value = "根据条件分页展示后台参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "type", value = "配置规则类型"),
            @ApiImplicitParam(name = "code", value = "配置规则代码"),
            @ApiImplicitParam(name = "name", value = "配置规则名称")
    })
    @PreAuthorize("hasAuthority('config_query')")
    public R<Page<Config>> findByPage(@ApiIgnore Page<Config> page, String type, String code, String name) {
        page.addOrder(OrderItem.desc("created"));
        Page<Config> configPage = configService.findByPage(page, type, code, name);
        return R.ok(configPage);
    }

    @PostMapping
    @ApiOperation(value = "新增一个配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "config", value = "config 新增config的json数据")
    })
    @PreAuthorize("hasAuthority('config_create')")
    public R add(@RequestBody @Validated Config config) {
        boolean save = configService.save(config);
        if (save) {
            return R.ok();
        }
        return R.fail("新增配置失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "config", value = "config 修改config的json数据")
    })
    @PreAuthorize("hasAuthority('config_update')")
    public R update(@RequestBody @Validated Config config) {
        boolean update = configService.updateById(config);
        if (update) {
            return R.ok();
        }
        return R.fail("修改配置失败");
    }
}
