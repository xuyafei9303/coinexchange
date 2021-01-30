package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.SysPrivilegeMapper;
import com.ixyf.domain.SysPrivilege;
import com.ixyf.service.SysPrivilegeService;
import org.springframework.util.CollectionUtils;

@Service
public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege> implements SysPrivilegeService{

    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;


    @Override
    public List<SysPrivilege> getAllSysPrivileges(Long menuId, Long roleId) {
        // 查询该菜单下的所有权限
        List<SysPrivilege> sysPrivilegeList = list(new LambdaQueryWrapper<SysPrivilege>().eq(SysPrivilege::getMenuId, menuId));
        if (CollectionUtils.isEmpty(sysPrivilegeList)) {
            return Collections.emptyList();
        }
        // 当前传递的角色是否包含该权限信息
        for (SysPrivilege sysPrivilege : sysPrivilegeList) {
            Set<Long> currentRoleSysPrivilegeIds = sysPrivilegeMapper.getPrivilegesByRoleId(roleId);
            if (currentRoleSysPrivilegeIds.contains(sysPrivilege.getId())) {
                sysPrivilege.setOwn(1); // 当前的角色是否有该权限
            }
        }
        return sysPrivilegeList;
    }
}
