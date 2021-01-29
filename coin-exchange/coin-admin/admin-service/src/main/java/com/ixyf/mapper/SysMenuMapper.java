package com.ixyf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ixyf.domain.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 通过用户的id查询用户的菜单
     * @param user_name_id
     * @return
     */
    List<SysMenu> selectMenusByUserId(Long user_name_id);
}