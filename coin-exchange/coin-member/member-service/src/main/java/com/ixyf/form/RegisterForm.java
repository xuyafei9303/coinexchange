package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "用户注册表单数据")
public class RegisterForm extends GeetestForm{

    @ApiModelProperty(value = "国家区号")
    @NotBlank
    private String countryCode;

    @ApiModelProperty(value = "邮箱")
    @NotBlank
    private String email;

    @ApiModelProperty(value = "手机号")
    @NotBlank
    private String mobile;

    @ApiModelProperty(value = "邀请码")
    @NotBlank
    private String invitionCode;

    @ApiModelProperty(value = "密码")
    @NotBlank
    private String password;

    @ApiModelProperty(value = "验证码 已弃用")
    private String validateCode;
}
