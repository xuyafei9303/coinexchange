package com.ixyf.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebLog {

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作用户
     */
    private String username;

    /**
     * 消耗时间 执行某个接口消耗的时间
     */
    private Integer spendTime;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * URI
     */
    private String uri;

    /**
     * URL
     */
    private String url;

    /**
     * 请求的ip
     */
    private String ip;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 返回结果
     */
    private Object result;
}
