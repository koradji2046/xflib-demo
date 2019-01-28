/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteSourceRedisProperties {

    private String source;
    private RedisProperties config;

    public RedisProperties getConfig() {
        return config;
    }

    public void setConfig(RedisProperties config) {
        this.config = config;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
