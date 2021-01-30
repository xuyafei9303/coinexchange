package com.ixyf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ixyf.domain.SysPrivilege;

import java.util.Set;

public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {
    /**
     * 使用角色的id查询权限
     * @param roleId
     * @return
     */
    Set<Long> getPrivilegesByRoleId(Long roleId);
}