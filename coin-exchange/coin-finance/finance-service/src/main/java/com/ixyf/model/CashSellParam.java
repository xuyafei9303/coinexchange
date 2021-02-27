package com.ixyf.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "GCN卖出的参数")
public class CashSellParam {

    @NotNull
    @ApiModelProperty(value = "要卖出的币id")
    private Long coinId;

    @NotNull
    @ApiModelProperty(value = "金额")
    private BigDecimal mum;

    @NotNull
    @ApiModelProperty(value = "数量")
    private BigDecimal num;

    @NotBlank
    @ApiModelProperty(value = "支付密码")
    private String payPassword;

    @NotBlank
    @ApiModelProperty(value = "验证码")
    private String validateCode;
}

