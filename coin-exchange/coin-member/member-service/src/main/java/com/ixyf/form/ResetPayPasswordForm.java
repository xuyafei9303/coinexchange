package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "重新设置交易密码")
public class ResetPayPasswordForm {

    @NotBlank
    @ApiModelProperty(value = "交易密码")
    private String payPassword;

    @NotBlank
    @ApiModelProperty(value = "验证码")
    private String validateCode;
}
