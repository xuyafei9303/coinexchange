package com.ixyf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "geetest")
public class GeetestProperties {

    /**
     * 极验id 3a01ffc01c1d63b37c3dbe8ee9555290
     */
    private String geetestId;

    /**
     * 极验key 27c7b4a18124d5d649b9c58ca1830871
     */
    private String geetestKey;
}
