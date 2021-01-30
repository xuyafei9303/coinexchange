package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ixyf.domain.SysMenu;
import com.ixyf.domain.SysPrivilege;
import com.ixyf.model.RolePrivilegesParam;
import com.ixyf.service.SysMenuService;
import com.ixyf.service.SysPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.SysRolePrivilegeMapper;
import com.ixyf.domain.SysRolePrivilege;
import com.ixyf.service.SysRolePrivilegeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService{

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    @Override
    public List<SysMenu> findSysMenuAndPrivileges(Long roleId) {
        // 先查询出所有的菜单信息
        List<SysMenu> sysMenuList = sysMenuService.list();
        // 页面要显示的是e二级菜单以及菜单所包含的权限
        if (CollectionUtils.isEmpty(sysMenuList)) {
            return Collections.emptyList();
        }
        // 一级菜单
        List<SysMenu> rootMenuList = sysMenuList.stream().filter(sysMenu -> sysMenu.getParentId() == null).collect(Collectors.toList());
        System.out.println("rootMenuList = " + rootMenuList);
        if (CollectionUtils.isEmpty(rootMenuList)) {
            return Collections.emptyList();
        }
        List<SysMenu> subMenu = new ArrayList<>();
        for (SysMenu sysMenu : rootMenuList) {
            subMenu.addAll(getChildMenus(sysMenu.getId(), roleId, sysMenuList));
        }
        return subMenu;
    }

    /**
     * 给角色授予权限
     * @param privilegesParam
     * @return
     */
    @Transactional
    @Override
    public boolean grantPrivileges(RolePrivilegesParam privilegesParam) {
        Long roleId = privilegesParam.getRoleId();
        // 先删除该角色之前的权限
        sysRolePrivilegeService.remove(new LambdaQueryWrapper<SysRolePrivilege>().eq(SysRolePrivilege::getRoleId, roleId));
        // 新增权限
        List<Long> privilegeIds = privilegesParam.getPrivilegeIds();
        if (CollectionUtils.isEmpty(privilegeIds)) {
            List<SysRolePrivilege> sysRolePrivilegeList = new ArrayList<>();
            for (Long privilegeId : privilegeIds) {
                SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                sysRolePrivilege.setRoleId(privilegesParam.getRoleId());
                sysRolePrivilege.setPrivilegeId(privilegeId);
                sysRolePrivilegeList.add(sysRolePrivilege);
            }
            System.out.println("sysRolePrivilegeList = " + sysRolePrivilegeList);
            return sysRolePrivilegeService.saveBatch(sysRolePrivilegeList);
        }
        return true;
    }

    /**
     * 查询菜单的子菜单
     * @param parentId 父菜单id
     * @param roleId 当前查询的角色id
     * @return
     */
    private List<SysMenu> getChildMenus(Long parentId, Long roleId, List<SysMenu> sources) {
        List<SysMenu> childs = new ArrayList<>();
        for (SysMenu sysMenu : sources) {
            if (sysMenu.getParentId() == parentId) { // 找子菜单
                childs.add(sysMenu);
                sysMenu.setChilds(getChildMenus(sysMenu.getId(), roleId, sources)); // 递归调用 给子菜单设置子菜单
                List<SysPrivilege> sysPrivilegeList = sysPrivilegeService.getAllSysPrivileges(sysMenu.getId(), roleId);
                sysMenu.setPrivileges(sysPrivilegeList);
            }
        }
        return childs;
    }


}
