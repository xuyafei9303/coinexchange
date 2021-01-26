package com.ixyf.config.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * swagger配置属性表
 */
@Data
@ConfigurationProperties(prefix = "swagger2")
public class SwaggerProperties {

    /**
     * 包扫描路径
     */
    private String basePackage;

    /**
     * 联系人名称
     */
    private String name;

    /**
     * 联系人主页
     */
    private String url;

    /**
     * 联系人邮箱
     */
    private String email;

    /**
     * api标题
     */
    private String title;

    /**
     * api描述
     */
    private String description;

    /**
     * api版本号
     */
    private String version;

    /**
     * api服务团队
     */
    private String termsOfServiceUrl;

}
