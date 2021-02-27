package com.ixyf.feign;

import com.ixyf.config.feign.OAuth2FeignClient;
import com.ixyf.dto.AdminBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "admin-service", path = "/adminBanks", configuration = OAuth2FeignClient.class)
public interface AdminBankServiceFeign {

    @GetMapping("/list")
    List<AdminBankDto> getAllAdminBanks();
}
