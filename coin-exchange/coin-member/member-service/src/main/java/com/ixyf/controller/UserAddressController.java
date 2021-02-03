package com.ixyf.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.UserAddress;
import com.ixyf.model.R;
import com.ixyf.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@Api(tags = "用户钱包地址")
@RequestMapping("/userAddress")
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    @GetMapping
    @ApiOperation(value = "查看用户钱包地址信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "userId", value = "用户id")
    })
    public R<Page<UserAddress>> findByPage(@ApiIgnore Page<UserAddress> page, Long userId) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<UserAddress> userAddressPage = userAddressService.findByPage(page, userId);
        return R.ok(userAddressPage);
    }
}
