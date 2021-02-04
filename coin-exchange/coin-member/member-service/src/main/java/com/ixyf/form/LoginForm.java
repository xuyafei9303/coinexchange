package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "登录参数")
public class LoginForm {

    /**
     * 极验相关
     */
    private String geetest_challenge;
    private String geetest_seccode;
    private String geetest_validate;

    @ApiModelProperty(value = "电话编号+86")
    private String countryCode;

    @NotBlank
    @ApiModelProperty(value = "用户密码")
    private String password;

    @NotBlank
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户uuid")
    private String uuid;

}
