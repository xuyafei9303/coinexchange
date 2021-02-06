package com.ixyf.geetest.utils;

import com.ixyf.geetest.GeetestLib;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GeetestProperties.class)
public class GeetestConfiguration {

    private GeetestProperties geetestProperties;

    public GeetestConfiguration(GeetestProperties geetestProperties) {
        this.geetestProperties = geetestProperties;
    }

    @Bean
    public GeetestLib geetestLib() {
        return new GeetestLib(
                geetestProperties.getGeetest_id(),  geetestProperties.getGeetest_key());
    }
}
