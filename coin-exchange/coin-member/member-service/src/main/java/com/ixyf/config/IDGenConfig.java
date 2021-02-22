package com.ixyf.config;

import cn.hutool.core.lang.Snowflake;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * id生成器 雪花算法
 */
@Data
public class IDGenConfig {

    /**
     * 机器码
     */
    @Value("${id.machine.code:0}")
    public static int machineCode;

    /**
     * 应用码
     */
    @Value("${id.app.code:0}")
    public static int appCode;

}
