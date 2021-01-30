package com.ixyf.service;

import com.ixyf.domain.SysPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPrivilegeService extends IService<SysPrivilege>{

    /**
     * 查询该菜单下面所有权限
     * @param menuId 菜单id
     * @param roleId 当前角色id
     * @return
     */
    List<SysPrivilege> getAllSysPrivileges(Long menuId, Long roleId);
}
