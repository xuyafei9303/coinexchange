package com.ixyf.controller;

import com.ixyf.model.R;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "coin-common的测试接口")
public class TestController {

    @GetMapping("/common/test")
    @ApiOperation(value = "测试方法testMethod()", authorizations = {@Authorization("Authorization")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "param1", value = "参数1", dataType = "String", paramType = "query", example = "示例1"),
            @ApiImplicitParam(name = "param2", value = "参数2", dataType = "String", paramType = "query", example = "示例2")
    })
    public R<String> testMethod(String param1, String param2) {
        return R.ok("ok");
    }
}
