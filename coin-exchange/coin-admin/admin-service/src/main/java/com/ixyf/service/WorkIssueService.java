package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.WorkIssue;
import com.baomidou.mybatisplus.extension.service.IService;
public interface WorkIssueService extends IService<WorkIssue>{


    /**
     * 根据条件分页查询工单
     * @param page 分页参数
     * @param status 工单状态
     * @param startTime 工单起始时间
     * @param endTime 工单截止时间
     * @return
     */
    Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime);
}
