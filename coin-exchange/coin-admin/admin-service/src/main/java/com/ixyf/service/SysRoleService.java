package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysRoleService extends IService<SysRole>{


    /**
     * 判断用户是否超级用户管理员
     * @param user_name_id
     * @return
     */
    boolean isSuperAdmin(Long user_name_id);

    /**
     * 根据角色name进行模糊查询展示页面
     * @param page
     * @param name
     * @return
     */
    Page<SysRole> findByPage(Page<SysRole> page, String name);
}
