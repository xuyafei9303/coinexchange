package com.ixyf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "现金交易的返回结果")
public class CashTradeVo {

    @NotBlank
    @ApiModelProperty("收款方户名")
    private String name;

    @NotBlank
    @ApiModelProperty("收款方开户行")
    private String bankName;

    @NotBlank
    @ApiModelProperty("收款方账号")
    private String bankCard;

    @NotNull
    @ApiModelProperty("转账金额")
    private BigDecimal amount;

    @NotBlank
    @ApiModelProperty("转账备注 参考号")
    private String remark;

    @NotNull
    @ApiModelProperty("状态")
    private Byte status;

}
