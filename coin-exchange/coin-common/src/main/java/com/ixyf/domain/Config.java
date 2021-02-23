package com.ixyf.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
    * 平台配置信息
    */
@ApiModel(value="com-ixyf-domain-Config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "config")
public class Config {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 配置规则类型
     */
    @NotBlank
    @TableField(value = "type")
    @ApiModelProperty(value="配置规则类型")
    private String type;

    /**
     * 配置规则代码
     */
    @NotBlank
    @TableField(value = "code")
    @ApiModelProperty(value="配置规则代码")
    private String code;

    /**
     * 配置规则名称
     */
    @NotBlank
    @TableField(value = "name")
    @ApiModelProperty(value="配置规则名称")
    private String name;

    /**
     * 配置规则描述
     */
    @NotBlank
    @TableField(value = "`desc`")
    @ApiModelProperty(value="配置规则描述")
    private String desc;

    /**
     * 配置值
     */
    @NotBlank
    @TableField(value = "value")
    @ApiModelProperty(value="配置值")
    private String value;

    /**
     * 创建时间
     */
    @TableField(value = "created", fill = FieldFill.INSERT)
    @ApiModelProperty(value="创建时间")
    private Date created;
}