package com.ixyf.model;

import com.ixyf.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * 登录结果展示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录结果展示")
public class LoginResult {

    /**
     * 登录成功的token 来自authorization-server里面生成的token
     */
    @ApiModelProperty(value = "用户登录成功的token 来自authorization-server里面生成的token")
    private String token;

    /**
     * 用户的菜单数据
     */
    @ApiModelProperty(value = "用户的菜单数据")
    private List<SysMenu> menus;

    /**
     * 用户拥有的权限数据
     */
    @ApiModelProperty(value = "用户拥有的权限数据")
    private List<SimpleGrantedAuthority> authorities;
}
