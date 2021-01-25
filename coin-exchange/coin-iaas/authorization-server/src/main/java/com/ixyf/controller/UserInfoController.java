package com.ixyf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {

    /**
     * 当前登陆的对象
     * @param principal
     * @return
     */
    /**
     * request:localhost:9999/user/info?access_token=9a178314-18ba-4570-b683-f2cf912a0270
     * response:
     *
     * {
     *     "authorities": [
     *         {
     *             "authority": "Role_Admin"
     *         }
     *     ],
     *     "details": {
     *         "remoteAddress": "0:0:0:0:0:0:0:1",
     *         "sessionId": null,
     *         "tokenValue": "9a178314-18ba-4570-b683-f2cf912a0270",
     *         "tokenType": "Bearer",
     *         "decodedDetails": null
     *     },
     *     "authenticated": true,
     *     "userAuthentication": {
     *         "authorities": [
     *             {
     *                 "authority": "Role_Admin"
     *             }
     *         ],
     *         "details": {
     *             "grant_type": "password",
     *             "username": "admin"
     *         },
     *         "authenticated": true,
     *         "principal": {
     *             "password": null,
     *             "username": "admin",
     *             "authorities": [
     *                 {
     *                     "authority": "Role_Admin"
     *                 }
     *             ],
     *             "accountNonExpired": true,
     *             "accountNonLocked": true,
     *             "credentialsNonExpired": true,
     *             "enabled": true
     *         },
     *         "credentials": null,
     *         "name": "admin"
     *     },
     *     "credentials": "",
     *     "principal": {
     *         "password": null,
     *         "username": "admin",
     *         "authorities": [
     *             {
     *                 "authority": "Role_Admin"
     *             }
     *         ],
     *         "accountNonExpired": true,
     *         "accountNonLocked": true,
     *         "credentialsNonExpired": true,
     *         "enabled": true
     *     },
     *     "oauth2Request": {
     *         "clientId": "coin-api",
     *         "scope": [
     *             "all"
     *         ],
     *         "requestParameters": {
     *             "grant_type": "password",
     *             "username": "admin"
     *         },
     *         "resourceIds": [],
     *         "authorities": [],
     *         "approved": true,
     *         "refresh": false,
     *         "redirectUri": null,
     *         "responseTypes": [],
     *         "extensions": {},
     *         "grantType": "password",
     *         "refreshTokenRequest": null
     *     },
     *     "clientOnly": false,
     *     "name": "admin"
     * }
     *
     * 或者在请求头添加 Authorization - bearer 9a178314-18ba-4570-b683-f2cf912a0270
     */
    @GetMapping("/user/info")
    public Principal userinfo(Principal principal) {
        // 使用ThreadLocal来实现的
        return principal;
    }
}
