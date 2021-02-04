package com.ixyf.geetest.utils;

import com.ixyf.config.GeetestProperties;
import com.ixyf.geetest.GeetestLib;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(GeetestProperties.class)
public class PropertiesUtils {

    private GeetestProperties geetestProperties;
    public PropertiesUtils(GeetestProperties geetestProperties) {
        this.geetestProperties = geetestProperties;
    }

    @Bean
    public GeetestLib geetestLib() {
        return new GeetestLib(geetestProperties.getGeetestId(), geetestProperties.getGeetestKey());
    }
}
