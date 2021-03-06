package com.ixyf.constant;

/**
 * 登录的一些常量
 */
public class LoginConstant {

    /**
     * 后台管理人员
     */
    public static final String ADMIN_TYPE = "admin_type";

    /**
     * 普通会员
     */
    public static final String MEMBER_TYPE = "member_type";

    /**
     * 超级管理员的角色code
     */
    public static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";

    public static final String REFRESH_TYPE = "REFRESH_TOKEN";

    /**
     * 使用用户名查询后台用户
     */
    public static final String QUERY_ADMIN_SQL =
            "SELECT `id` ,`username` ,`password` ,`status` FROM sys_user WHERE username = ? ";

    /**
     * 查询用户角色code，判断用户是否为管理员
     */
    public static final String QUERY_ROLE_CODE_SQL =
            "SELECT `code` FROM sys_role LEFT JOIN sys_user_role ON sys_role.id = sys_user_role.role_id WHERE sys_user_role.user_id = ? ";

    /**
     * 从权限表中查出所有权限的名称
     */
    public static final String QUERY_ALL_PERMISSIONS_SQL =
            "SELECT `name` FROM sys_privilege";

    /**
     * 非超级用户，需要先查询出code -> permissionID -> permission
     */
    public static final String QUERY_PERMISSION_SQL =
            "SELECT `name` FROM sys_privilege " +
                    "LEFT JOIN sys_role_privilege " +
                    "ON sys_role_privilege.privilege_id = sys_privilege.id " +
                    "LEFT JOIN sys_user_role " +
                    "ON sys_role_privilege.role_id = sys_user_role.role_id " +
                    "WHERE sys_user_role.user_id = ? ";

    /**
     * 通过手机号或者邮箱查询会员
     */
    public static final String QUERY_MEMBER_SQL =
            "SELECT `id`, `password`, `status` FROM `user` WHERE `mobile` = ? OR `email` = ?" ;

    /**
     * 使用后台用户的id查询用户名称
     */
    public static final String QUERY_ADMIN_USER_WITH_ID_SQL =
            "SELECT `username` FROM sys_user WHERE id = ?" ;

    /**
     * 使用会员用户的id查询用户mobile
     */
    public static final String QUERY_MEMBER_USER_WITH_ID_SQL =
            "SELECT `mobile` FROM user WHERE id = ? " ;

}
