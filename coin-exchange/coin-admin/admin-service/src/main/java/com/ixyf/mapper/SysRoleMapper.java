package com.ixyf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ixyf.domain.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 获取用户角色code的实现
     * @param user_name_id
     * @return
     */
    String getUserRoleCode(Long user_name_id);
}