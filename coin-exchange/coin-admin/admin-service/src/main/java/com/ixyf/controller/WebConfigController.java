package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.WebConfig;
import com.ixyf.model.R;
import com.ixyf.service.WebConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * web_config_query
 * web_config_delete
 * web_config_update
 * web_config_create
 */
@RestController
@Api(tags = "资源配置控制器")
@RequestMapping("/webConfigs")
public class WebConfigController {

    @Resource
    private WebConfigService webConfigService;

    @GetMapping
    @ApiOperation(value = "分页查询资源配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "name", value = "资源名称"),
            @ApiImplicitParam(name = "type", value = "资源类型")
    })
    @PreAuthorize("hasAuthority('web_config_query')")
    public R<Page<WebConfig>> findByPage(@ApiIgnore Page<WebConfig> page, String name, String type) {
        page.addOrder(OrderItem.desc("created"));
        Page<WebConfig> webConfigPage = webConfigService.findByPage(page, name, type);
        return R.ok(webConfigPage);
    }

    @PostMapping
    @ApiOperation(value = "新增配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "webConfig", value = "webConfig的json数据")
    })
    @PreAuthorize("hasAuthority('web_config_create')")
    public R add(@RequestBody @Validated WebConfig webConfig) {
        boolean save = webConfigService.save(webConfig);
        if (save) {
            return R.ok();
        }
        return R.fail("新增资源配置失败");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "webConfig的ids集合")
    })
    @PreAuthorize("hasAuthority('web_config_delete')")
    public R delete(@RequestBody String [] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选择要删除的配置项");
        }
        boolean removeByIds = webConfigService.removeByIds(Arrays.asList(ids));
        if (removeByIds) {
            return R.ok();
        }
        return R.fail("删除配置项失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "webConfig", value = "webConfig的json数据")
    })
    @PreAuthorize("hasAuthority('web_config_update')")
    public R update(@RequestBody @Validated WebConfig webConfig) {
        boolean update = webConfigService.updateById(webConfig);
        if (update) {
            return R.ok();
        }
        return R.fail("修改资源配置失败");
    }
}
