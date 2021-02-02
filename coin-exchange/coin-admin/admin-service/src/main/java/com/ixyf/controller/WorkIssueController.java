package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.WorkIssue;
import com.ixyf.model.R;
import com.ixyf.service.WorkIssueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * work_issue_query
 * work_issue_update
 *
 */
@RestController
@Api(tags = "客服工单")
@RequestMapping("/workIssues")
public class WorkIssueController {

    @Resource
    private WorkIssueService workIssueService;

    @GetMapping
    @ApiOperation(value = "客服工单的分页展示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "status", value = "工单的状态"),
            @ApiImplicitParam(name = "startTime", value = "工单创建的时间"),
            @ApiImplicitParam(name = "endTime", value = "工单创建结束的时间")
    })
    @PreAuthorize("hasAuthority('work_issue_query')")
    public R<Page<WorkIssue>> findByPage(@ApiIgnore Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<WorkIssue> workIssuePage = workIssueService.findByPage(page, status, startTime, endTime);
        return R.ok(workIssuePage);
    }

    @PatchMapping
    @ApiOperation(value = "回复客服工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工单的id"),
            @ApiImplicitParam(name = "answer", value = "回复的内容")
    })
    @PreAuthorize("hasAuthority('work_issue_update')")
    public R answer(Long id, String answer) {
        WorkIssue workIssue = new WorkIssue();
        workIssue.setId(id);
        workIssue.setAnswer(answer);
        String userStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        workIssue.setAnswerUserId(Long.valueOf(userStr));
        boolean update = workIssueService.updateById(workIssue);
        if (update) {
            return R.ok();
        }
        return R.fail("回复工单失败");
    }
}
