package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.NoticeMapper;
import com.ixyf.domain.Notice;
import com.ixyf.service.NoticeService;
import org.springframework.util.StringUtils;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{

    @Override
    public Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, Integer status) {
        return page(page, new LambdaQueryWrapper<Notice>()
                .like(!StringUtils.isEmpty(title), Notice::getTitle, title)
                .between(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime), Notice::getCreated, startTime, endTime + " 23:59:59")
                .eq(status != null, Notice::getStatus, status)
        );
    }
}
