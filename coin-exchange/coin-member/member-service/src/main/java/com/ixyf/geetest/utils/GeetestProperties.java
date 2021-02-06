package com.ixyf.geetest.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "geetest")
public class GeetestProperties {

    /**
     * geetest:
     *   geetest-id: 3a01ffc01c1d63b37c3dbe8ee9555290
     *   geetest-key: 27c7b4a18124d5d649b9c58ca1830871
     */
    private String geetest_id;

    private String geetest_key;

}
