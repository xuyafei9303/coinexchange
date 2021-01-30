package com.ixyf.service;

import com.ixyf.domain.SysMenu;
import com.ixyf.domain.SysRolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ixyf.model.RolePrivilegesParam;

import java.util.List;

public interface SysRolePrivilegeService extends IService<SysRolePrivilege>{


    /**
     * 查询角色的权限
     * @param roleId
     * @return
     */
    List<SysMenu> findSysMenuAndPrivileges(Long roleId);

    boolean grantPrivileges(RolePrivilegesParam privilegesParam);
}
