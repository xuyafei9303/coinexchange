package com.ixyf.config.resource;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@EnableResourceServer
@EnableMethodCache(basePackages = "com.ixyf.service.impl")
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().disable()
                .authorizeRequests()
                .antMatchers(
                        "/v2/api-docs",
                        "/swagger-resources/configuration/ui",//用来获取支持的动作
                        "/swagger-resources",//用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/webjars/**",
                        "/swagger-ui.html"

                ).permitAll()
                .antMatchers("/**").authenticated()
                .and().headers().cacheControl();
    }

    /**
     * 设置公钥
     *
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(resourceJwtTokenStore());
    }

    private TokenStore resourceJwtTokenStore() {
        return new JwtTokenStore(resourceJwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter resourceJwtAccessTokenConverter() {
        // resource 验证token（公钥）  authorization 产生token（私钥）
        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        String str = null;
        try {
            final ClassPathResource classPathResource = new ClassPathResource("coinexchange.txt");
            final byte[] bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            str = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        jwtAccessTokenConverter.setVerifierKey(str);
        return jwtAccessTokenConverter;
    }
}
