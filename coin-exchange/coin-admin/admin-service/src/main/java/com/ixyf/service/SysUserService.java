package com.ixyf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysUserService extends IService<SysUser>{


    /**
     * 分页展示员工信息
     * @param page
     * @param mobile 员工手机号
     * @param fullname 员工全称
     * @return
     */
    Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname);

    /**
     * 新增员工
     * @param sysUser
     * @return
     */
    boolean addUser(SysUser sysUser);
}
