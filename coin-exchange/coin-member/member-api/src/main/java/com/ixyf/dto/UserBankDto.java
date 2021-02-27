package com.ixyf.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户银行卡传输对象")
public class UserBankDto {

    /**
     * 开户人
     */
    @ApiModelProperty(value="开户人")
    @NotBlank
    private String realName;

    /**
     * 开户行
     */
    @ApiModelProperty(value="开户行")
    @NotBlank
    private String bank;

    /**
     * 开户省
     */
    @ApiModelProperty(value="开户省")
    private String bankProv;

    /**
     * 开户市
     */
    @ApiModelProperty(value="开户市")
    private String bankCity;

    /**
     * 开户地址
     */
    @ApiModelProperty(value="开户地址")
    private String bankAddr;

    /**
     * 开户账号
     */
    @ApiModelProperty(value="开户账号")
    @NotBlank
    private String bankCard;
}
