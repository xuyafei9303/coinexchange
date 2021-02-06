package com.ixyf.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户的身份认证信息")
public class UserAuthForm extends GeetestForm{

    @NotBlank
    @ApiModelProperty(value = "用户的真实名称")
    private String realName;

    @NotBlank
    @ApiModelProperty(value = "身份证号码")
    private String idCard;

    @NotBlank
    @ApiModelProperty(value = "极验类型")
    private Integer idCardType;

}
