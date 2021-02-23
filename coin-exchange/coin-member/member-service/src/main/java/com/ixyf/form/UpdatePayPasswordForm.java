package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "修改交易密码")
public class UpdatePayPasswordForm {

    @NotBlank
    @ApiModelProperty(value = "原始密码")
    private String oldpassword;

    @NotBlank
    @ApiModelProperty(value = "新密码")
    private String newpassword;

    @NotBlank
    @ApiModelProperty(value = "手机验证码")
    private String validateCode;
}
