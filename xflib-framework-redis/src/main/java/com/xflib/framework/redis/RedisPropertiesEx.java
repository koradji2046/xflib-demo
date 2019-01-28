/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class RedisPropertiesEx {

    private String site;
    private RedisProperties config;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public RedisProperties getConfig() {
        return config;
    }

    public void setConfig(RedisProperties config) {
        this.config = config;
    }
}
