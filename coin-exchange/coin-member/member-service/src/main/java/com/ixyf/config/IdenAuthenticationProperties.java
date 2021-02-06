package com.ixyf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 身份证认证
 */
@Data
@ConfigurationProperties(prefix = "identifier")
public class IdenAuthenticationProperties {

    /**
     * 身份认证的url地址 http://idenauthen.market.alicloudapi.com
     */
    private String url;

    /**
     * 路径 /idenAuthentication
     */
    private String path;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 203912541
     */
    private String appKey;

    /**
     * nyggJ2gVN5a4353VnzkyeMvQEYIvVcUe
     */
    private String appSecret;

    /**
     * de30907194e043a59266dcb482642e94
     */
    private String appCode;
}
