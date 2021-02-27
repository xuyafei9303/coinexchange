package com.ixyf.feign;

import com.ixyf.config.feign.OAuth2FeignClient;
import com.ixyf.dto.UserBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 若feignClient里面的name相同时，spring创建对象就会报错,他认为他们两个对象是一样的
 */
@FeignClient(name = "member-service", contextId = "userBankServiceFeign", path = "/userBanks", configuration = OAuth2FeignClient.class)
public interface UserBankServiceFeign {

    @GetMapping("/{userId/info}")
    UserBankDto getUserBankInfo(@PathVariable Long userId);
}
