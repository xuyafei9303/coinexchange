package com.ixyf.service;

import com.ixyf.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{


    /**
     * 通过用户的id 查询用户的菜单数据
     * @param user_name_id 在jwt中 id 就是 user_name
     * @return
     */
    List<SysMenu> getMenusByUserId(Long user_name_id);
}
