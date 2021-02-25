package com.ixyf.controller;

import com.ixyf.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "财务系统的测试类")
public class TestController {

    @GetMapping("/test")
    @ApiOperation("财务系统的测试接口")
    public R test() {
        return R.ok("财务系统测试通过");
    }
}
