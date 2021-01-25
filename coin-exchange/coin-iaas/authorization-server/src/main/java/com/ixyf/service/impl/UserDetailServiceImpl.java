package com.ixyf.service.impl;

import com.ixyf.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自己实现的UserDetailService
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        final String login_type = requestAttributes.getRequest().getParameter("login_type"); // 区分是后台人员还是普通用户
        if (StringUtils.isEmpty(login_type)) {
            throw new AuthenticationServiceException("登录类型不能为空");
        }
        UserDetails userDetails = null;
        try {
            final String grant_token = requestAttributes.getRequest().getParameter("grant_type"); // refresh_token
            if (LoginConstant.REFRESH_TYPE.equals(grant_token.toUpperCase())) {
                username = adJustUsername(username, login_type);
            }
            switch (login_type) {
                case LoginConstant.ADMIN_TYPE:
                    userDetails = loadSysUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE:
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("暂不支持此种方式登录: " + login_type);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UsernameNotFoundException("该用户不存在: " + username);
        }
        return userDetails;
    }

    /**
     * 纠正用户的名称
     * @param username 用户的id
     * @param login_type admin_type or member_type
     * @return
     */
    private String adJustUsername(String username, String login_type) {
        if (LoginConstant.ADMIN_TYPE.equals(login_type)) {
            // 管理员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_USER_WITH_ID_SQL, String.class, username);
        }
        if (LoginConstant.MEMBER_TYPE.equals(login_type)) {
            // 会员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_USER_WITH_ID_SQL, String.class, username);
        }
        return username;
    }

    /**
     * 会员登录
     * @param username
     * @return
     */
    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                if (resultSet.wasNull()) {
                    throw new UsernameNotFoundException("用户:" + username + "不存在");
                }
                final long id = resultSet.getLong("id"); // 用户id
                final String password = resultSet.getString("password"); // 用户密码
                final int status = resultSet.getInt("status"); // 用户状态
                return new User(
                        String.valueOf(id), // 使用id代替username
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        }, username, username);
    }

    /**
     * 后台登录
     * @param username
     * @return
     */
    private UserDetails loadSysUserByUsername(String username) {

        // 使用用户名查询用户
       return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                if (resultSet.wasNull()) {
                    throw new UsernameNotFoundException("该用户不存在: " + username);
                }
                final long id = resultSet.getLong("id"); // 用户的id
                final String password = resultSet.getString("password"); // 用户的密码
                final int status = resultSet.getInt("status"); // 用户的状态
                return new User( // 封装成一个userDetail对象返回
                        String.valueOf(id), // 使用id代替username
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        getSysUserPermissions(id)

                );
            }
        }, username);

    }

    /**
     * 通过用户的id查询用户的权限数据
     * @param id
     * @return
     */
    private Collection<? extends GrantedAuthority> getSysUserPermissions(long id) {

        // 1. 当用户为超级管理员时，拥有所有的权限数据
        final String roleCode = jdbcTemplate.queryForObject(LoginConstant.QUERY_ROLE_CODE_SQL, String.class, id);
        List<String> permissions = null; // 权限名称
        if (LoginConstant.ADMIN_ROLE_CODE.equals(roleCode)) { // 超级管理员
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS_SQL, String.class);
        } else { // 2. 当用户为偶同用户时，需要用角色来查询权限数据
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
        }
        if (permissions.isEmpty()) {
            return Collections.emptySet();
        }
        return permissions.stream()
                .distinct() // 去重
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

    }
}
