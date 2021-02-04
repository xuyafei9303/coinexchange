package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录成功返回的数据")
public class LoginUser {

    @ApiModelProperty(value = "用户名")
    private String username;

    private String access_token;

    private String refresh_token;

    @ApiModelProperty(value = "token过期时间")
    private Long expire;
}
