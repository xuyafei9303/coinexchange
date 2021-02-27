package com.ixyf.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "银行卡参数")
public class AdminBankDto {

    @NotBlank
    @ApiModelProperty(value = "开户人名称")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "开户行名称")
    private String bankName;

    @NotBlank
    @ApiModelProperty(value = "银行卡号")
    private String bankCard;
}
