package com.ixyf.feign;

import com.ixyf.config.feign.OAuth2FeignClient;
import com.ixyf.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "member-service", configuration = OAuth2FeignClient.class, path = "/users")
public interface UserServiceFeign {

    /**
     * 远程调用 admin-service里面调用member-service
     * @param ids
     * @return
     */
    @GetMapping("/basic/users")
    List<UserDto> getBasicUsers(@RequestParam("ids") List<Long> ids);
}
