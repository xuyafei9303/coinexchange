package com.ixyf.service;

import com.ixyf.domain.UserAuthAuditRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthAuditRecordService extends IService<UserAuthAuditRecord>{


    /**
     * 根据用户id查询该用户的审核信息
     * @param id
     * @return
     */
    List<UserAuthAuditRecord> getUserAuthAuditRecordList(Long id);
}
