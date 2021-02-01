package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Notice;
import com.ixyf.model.R;
import com.ixyf.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 权限：
 * notice_query
 * notice_delete
 * notice_create
 * notice_update
 */
@RequestMapping("/notices")
@Api(tags = "公告管理")
@RestController
public class SysNoticeController {

    @Resource
    private NoticeService noticeService;

    @GetMapping
    @ApiOperation(value = "分页查询广告")
    @PreAuthorize("hasAuthority('notice_query')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "title", value = "公告标题"),
            @ApiImplicitParam(name = "startTime", value = "公告创建的开始时间"),
            @ApiImplicitParam(name = "endTime", value = "公告创建的结束时间")
    })
    public R<Page<Notice>> findByPage(@ApiIgnore Page<Notice> page, String title, String startTime, String endTime, Integer status) {
        page.addOrder(OrderItem.desc("last_update_time"));
        return R.ok(noticeService.findByPage(page, title, startTime, endTime, status));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一个公告")
    @PreAuthorize("hasAuthority('notice_delete')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的公告ids集合")
    })
    public R delete(@RequestBody String [] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("请选择要删除的公告");
        }
        boolean removeByIds = noticeService.removeByIds(Arrays.asList(ids));
        if (removeByIds) {
            return R.ok();
        }
        return R.fail("删除失败");
    }

    @PostMapping("/updateStatus")
    @ApiOperation(value = "启用or禁用一个公告")
    @PreAuthorize("hasAuthority('notice_update')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id"),
            @ApiImplicitParam(name = "status", value = "公告的状态")
    })
    public R updateStatus(Long id,Integer status) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setStatus(status);
        boolean update = noticeService.updateById(notice); // 局部更新，只修改不为空的值
        if (update) {
            return R.ok("更新状态成功");
        }
        return R.fail("更新状态失败");
    }

    @PostMapping()
    @ApiOperation(value = "新增一个公告")
    @PreAuthorize("hasAuthority('notice_create')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice", value = "新增公告的json数据")
    })
    public R add(@RequestBody Notice notice) {
        notice.setStatus(1);
        boolean save = noticeService.save(notice);
        if (save) {
            return R.ok();
        }
        return R.fail("新增公告失败");
    }

    @PatchMapping()
    @ApiOperation(value = "修改一个公告")
    @PreAuthorize("hasAuthority('notice_update')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "notice", value = "修改公告的json数据")
    })
    public R update(@RequestBody Notice notice) {
        boolean update = noticeService.updateById(notice);
        if (update) {
            return R.ok();
        }
        return R.fail("修改公告失败");
    }
}
