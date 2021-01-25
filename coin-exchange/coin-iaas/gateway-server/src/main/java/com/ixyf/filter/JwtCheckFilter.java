package com.ixyf.filter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${no.require.uris:/admin/login}")
    private Set<String> noRequireTokenUris;

    /**
     * 过滤器拦截到用户请求以后做什么
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 查看该接口是否需要token访问
        if (!isRequireToken(exchange)) {
            return chain.filter(exchange); // 不需要token直接放行
        }
        // 2. 取出用户的token
        String token = getUserToken(exchange);
        // 3. 判断用户的token是否还有效
        if (StringUtils.isEmpty(token)) {
            return buildNoAuthorizationResult(exchange);
        }
        final Boolean hasKey = stringRedisTemplate.hasKey(token);
        if (hasKey != null && hasKey) {
            return chain.filter(exchange); // token有效，直接放行
        }
        return buildNoAuthorizationResult(exchange);
    }

    /**
     * 给用户响应一个没有token的错误
     * @param exchange
     * @return
     */
    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        final ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("Content-type", "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "NoAuthorization");
        jsonObject.put("errorMsg", "Token is null or fail");
        final DataBuffer dataBuffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return response.writeWith(Flux.just(dataBuffer));
    }


    /**
     * 判断是否需要token
     * @param exchange
     * @return
     */
    private boolean isRequireToken(ServerWebExchange exchange) {
        final String path = exchange.getRequest().getURI().getPath();
        if (noRequireTokenUris.contains(path)) {
            return false; // 不需要token
        }
        return Boolean.TRUE;
    }

    /**
     * 取出用户的token
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        final String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token == null ? null : token.replace("bearer ", "");

    }

    /**
     * 拦截器顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
