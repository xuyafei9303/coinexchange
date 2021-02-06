package com.ixyf.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixyf.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.service.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(IdenAuthenticationProperties.class)
public class IdenAuthenticationConfiguration {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static IdenAuthenticationProperties idenAuthenticationProperties;
    public IdenAuthenticationConfiguration(IdenAuthenticationProperties properties) {
        IdenAuthenticationConfiguration.idenAuthenticationProperties = properties;
    }

    /**
     * 用户信息的实名认证
     * @param realName 用户真实姓名
     * @param cardNumber 用户身份证号码
     * @return
     */
    public static boolean check(String realName, String cardNumber) {
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("idNo", cardNumber);
        postParameters.add("name", realName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Authorization", "APPCODE " + idenAuthenticationProperties.getAppCode());
        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                idenAuthenticationProperties.getUrl() + idenAuthenticationProperties.getPath(),
                HttpMethod.POST,
                r,
                String.class
        );
        System.out.println("responseEntity = " + responseEntity);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String entityBody = responseEntity.getBody();
            JSONObject jsonObject = JSON.parseObject(entityBody);
            Object respCode = jsonObject.get("respCode");
            System.out.println("respCode = " + respCode);
            return "0000".equals(respCode);
        }
        return false;
    }
}
