package com.ixyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysUserRole;
import com.ixyf.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.SysUser;
import com.ixyf.mapper.SysUserMapper;
import com.ixyf.service.SysUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname) {
        Page<SysUser> userPage = page(page,
                new LambdaQueryWrapper<SysUser>()
                        .like(!StringUtils.isEmpty(mobile), SysUser::getMobile, mobile)
                        .like(!StringUtils.isEmpty(fullname), SysUser::getFullname, fullname)
        );
        List<SysUser> records = userPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            for (SysUser record : records) {
                List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, record.getId()));
                if (!CollectionUtils.isEmpty(userRoles)) {
                    record.setRole_strings(userRoles.stream().map(userRole -> userRole.getRoleId().toString()).collect(Collectors.joining(",")));
                }
            }
        }
        return userPage;
    }

    @Transactional
    @Override
    public boolean addUser(SysUser sysUser) {
        String password = sysUser.getPassword();
        String role_strings = sysUser.getRole_strings();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(password);
        sysUser.setPassword(encode); // 设置加密后的密码
        boolean save = super.save(sysUser);
        if (save) {
            // 给用户新增角色数据
            if (!StringUtils.isEmpty(role_strings)) {
                String[] roleIds = role_strings.split(",");
                List<SysUserRole> userRoleList = new ArrayList<>(roleIds.length);
                for (String roleId : roleIds) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setRoleId(Long.valueOf(roleId));
                    userRole.setUserId(sysUser.getId());
                    userRoleList.add(userRole);
                }
                sysUserRoleService.saveBatch(userRoleList);
            }
        }
        return save;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        boolean remove = super.removeByIds(idList);
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, idList));
        return remove;
    }
}
