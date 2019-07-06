/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.redisson;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import com.xflib.framework.redis.redisson.configure.RedissonProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteSourceRedissonProperties {

    private String source;
    private RedisProperties redis;
    private RedissonProperties config;

    public RedissonProperties getConfig() {
        return config;
    }

    public void setConfig(RedissonProperties config) {
        this.config = config;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

	public RedisProperties getRedis() {
		return redis;
	}

	public void setRedis(RedisProperties redis) {
		this.redis = redis;
	}
}
