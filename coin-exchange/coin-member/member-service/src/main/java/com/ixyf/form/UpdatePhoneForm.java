package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "修改手机号的表单")
public class UpdatePhoneForm {

    @NotBlank
    @ApiModelProperty(value = "国家区号")
    private String countryCode;

    @NotBlank
    @ApiModelProperty(value = "新手机号")
    private String newMobilePhone;

    @NotBlank
    @ApiModelProperty(value = "新手机号验证码")
    private String ValidateCode;

    @NotBlank
    @ApiModelProperty(value = "旧手机号验证码")
    private String oldValidateCode;
}
