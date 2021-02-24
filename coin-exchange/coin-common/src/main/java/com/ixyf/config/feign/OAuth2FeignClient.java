package com.ixyf.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class OAuth2FeignClient implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.info("没有上下文，无法传递token");
        }
        assert requestAttributes != null;
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(header)) {
            requestTemplate.header(HttpHeaders.AUTHORIZATION, header);
            log.info("本次token传递成功，token的值为：{}", header);
        }
    }
}
