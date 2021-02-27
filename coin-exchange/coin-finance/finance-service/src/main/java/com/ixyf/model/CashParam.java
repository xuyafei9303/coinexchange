package com.ixyf.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "GCN购买参数")
public class CashParam {

    @NotNull
    @ApiModelProperty("币种id")
    private Long coinId;

    @NotNull
    @ApiModelProperty("币种数量")
    private BigDecimal num;

    @NotNull
    @ApiModelProperty("币种金额")
    private BigDecimal mum;
}
