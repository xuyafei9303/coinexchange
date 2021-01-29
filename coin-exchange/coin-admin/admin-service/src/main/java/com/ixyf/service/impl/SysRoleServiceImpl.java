package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.SysRole;
import com.ixyf.mapper.SysRoleMapper;
import com.ixyf.service.SysRoleService;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public boolean isSuperAdmin(Long user_name_id) {
        // 当用户的角色code为role_admin,该用户为超级管理员
        // 用户id -> 用户角色 -> 判断该角色code是否为role_admin
        String role_code = sysRoleMapper.getUserRoleCode(user_name_id);
        if (!StringUtils.isEmpty(role_code) && role_code.equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    @Override
    public Page<SysRole> findByPage(Page<SysRole> page, String name) {
        return page(page, new LambdaQueryWrapper<SysRole>().like(
                !StringUtils.isEmpty(name),
                SysRole::getName,
                name
        ));
    }
}
