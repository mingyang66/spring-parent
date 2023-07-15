package com.emily.infrastructure.logger.common;

/**
 * ---------------------------------------------
 * 压缩比率如下：
 * .gz  1/5  10KB压缩后2KB
 * .zip  2/11 11KB压缩后2KB
 * ---------------------------------------------
 *
 * @Description :  压缩模式
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/15 9:55 AM
 */
public enum CompressionMode {
    NONE(""), GZ(".gz"), ZIP(".zip");
    String suffix;

    CompressionMode(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}