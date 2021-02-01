package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import org.aspectj.weaver.ast.Not;

public interface NoticeService extends IService<Notice>{


    /**
     * 条件查询广告
     * @param page 分页参数
     * @param title 根据标题查询
     * @param startTime 根据公告创建时间查询
     * @param endTime 根据公告结束时间查询
     * @param status 根据公告当前状态查询
     * @return
     */
    Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status);
}
