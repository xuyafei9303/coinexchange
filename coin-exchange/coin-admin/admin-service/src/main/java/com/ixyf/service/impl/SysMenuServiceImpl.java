package com.ixyf.service.impl;

import com.ixyf.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.mapper.SysMenuMapper;
import com.ixyf.domain.SysMenu;
import com.ixyf.service.SysMenuService;
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 根据user_name_id查询用户菜单数据
     * @param user_name_id 在jwt中 id 就是 user_name
     * @return
     */
    @Override
    public List<SysMenu> getMenusByUserId(Long user_name_id) {
        // 1. 如果是超级管理员，拥有所有权限
        if (sysRoleService.isSuperAdmin(user_name_id)) {
            return list();
        }

        // 2. 如果是普通用户，通过角色查询菜单，拥有部分权限
        return sysMenuMapper.selectMenusByUserId(user_name_id);
    }
}
