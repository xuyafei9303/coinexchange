package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "重置登录密码表单")
public class ResetPasswordForm extends GeetestForm {

    @NotBlank
    @ApiModelProperty(value = "国家区号")
    private String countryCode;

    @NotBlank
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotBlank
    @ApiModelProperty(value = "新密码")
    private String password;

    @NotBlank
    @ApiModelProperty(value = "手机验证码")
    private String validateCode;
}
