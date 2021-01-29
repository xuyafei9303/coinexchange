package com.ixyf.service;

import com.ixyf.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysRoleService extends IService<SysRole>{


    /**
     * 判断用户是否超级用户管理员
     * @param user_name_id
     * @return
     */
    boolean isSuperAdmin(Long user_name_id);
}
