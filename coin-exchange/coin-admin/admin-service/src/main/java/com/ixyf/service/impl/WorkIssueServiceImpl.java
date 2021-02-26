package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.dto.UserDto;
import com.ixyf.feign.UserServiceFeign;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.WorkIssueMapper;
import com.ixyf.domain.WorkIssue;
import com.ixyf.service.WorkIssueService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class WorkIssueServiceImpl extends ServiceImpl<WorkIssueMapper, WorkIssue> implements WorkIssueService{

    @Resource
    private UserServiceFeign userServiceFeign;

    @Override
    public Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        Page<WorkIssue> workIssuePage = page(page, new LambdaQueryWrapper<WorkIssue>()
                .eq(status != null, WorkIssue::getStatus, status)
                .between(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime), WorkIssue::getCreated, startTime, endTime + " 23:59:59")
        );
        List<WorkIssue> records = workIssuePage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return workIssuePage;
        }
        // 远程调用member-service
        // 先批量收集id
        List<Long> userIds = records.stream().map(WorkIssue::getUserId).collect(Collectors.toList());
        // 远程调用
//        List<UserDto> basicUsers = userServiceFeign.getBasicUsers(userIds);
//        if (CollectionUtils.isEmpty(basicUsers)) {
//            return workIssuePage;
//        }
//        Map<Long, UserDto> idMapUserDtos = basicUsers.stream().collect(
//                        Collectors.toMap(
//                                UserDto::getId,  // key
//                                userDto -> userDto // value
//                        )
//                );
        Map<Long, UserDto> basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
        records.forEach(workIssue -> { // 循环每一个workIssue 并给他设置用户信息
            UserDto userDto = basicUsers.get(workIssue.getUserId());
            workIssue.setUsername(userDto == null ? "测试用户" : userDto.getUsername());
            workIssue.setRealName(userDto == null ? "测试用户" : userDto.getRealName());
        });

        return workIssuePage;
    }
}
