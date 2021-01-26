package com.ixyf.config.swagger;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 因为token无法在swagger ui页面进行验证，所以升级版本到2.9.2
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    private SwaggerProperties swaggerProperties;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    /**
     * 文档类型
     *
     * @return
     */
    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
        // 安全配置
        docket.securityContexts(securityContexts()) // 安全规则上下文
                .securitySchemes(securitySchemes()) // 安全规则
        ;
        return docket;
    }

    /**
     * 安全的规则
     *
     * @return
     */
    private List<? extends SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey("Authorization", "Authorization", "Authorization"));
    }

    /**
     * 安全的上下文
     *
     * @return
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(new SecurityContext(
                Collections.singletonList(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "accessResource")})),
                PathSelectors.any()
        ));
    }

    /**
     * api信息的简介
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().contact(
                new Contact(swaggerProperties.getName(), swaggerProperties.getUrl(), swaggerProperties.getEmail())
        )
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .build();
    }
}
