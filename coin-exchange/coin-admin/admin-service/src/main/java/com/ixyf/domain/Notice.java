package com.ixyf.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
    * 系统资讯公告信息
    */
@ApiModel(value="com-ixyf-domain-Notice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "notice")
public class Notice {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 标题
     */
    @NotBlank
    @TableField(value = "title")
    @ApiModelProperty(value="标题")
    private String title;

    /**
     * 简介
     */
    @NotBlank
    @TableField(value = "description")
    @ApiModelProperty(value="简介")
    private String description;

    /**
     * 作者
     */
    @NotBlank
    @TableField(value = "author")
    @ApiModelProperty(value="作者")
    private String author;

    /**
     * 文章状态
     */
    @TableField(value = "status")
    @ApiModelProperty(value="文章状态")
    private Integer status;

    /**
     * 文章排序，越大越靠前
     */
    @NotNull
    @TableField(value = "sort")
    @ApiModelProperty(value="文章排序，越大越靠前")
    private Integer sort;

    /**
     * 内容
     */
    @NotBlank
    @TableField(value = "content")
    @ApiModelProperty(value="内容")
    private String content;

    /**
     * 最后修改时间
     */
    @TableField(value = "last_update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value="最后修改时间")
    private Date lastUpdateTime;

    /**
     * 创建日期
     */
    @TableField(value = "created", fill = FieldFill.INSERT)
    @ApiModelProperty(value="创建日期")
    private Date created;
}