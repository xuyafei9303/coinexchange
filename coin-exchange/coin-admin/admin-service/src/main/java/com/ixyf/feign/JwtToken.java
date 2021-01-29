package com.ixyf.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * authorization-server传递过来的对象
 */
@Data
public class JwtToken {

    @JsonProperty("access_token")
    private String accessToken;

    /**
     * token类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 可以刷新的token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * token过期时间
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * token范围
     */
    private String scope;

    /**
     * token的颁发凭证
     */
    private String jti;


}
