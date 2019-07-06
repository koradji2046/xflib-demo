package com.xflib.framework.redis.redisson.configure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xflib.framework.redis.redisson.SiteRedissonProperties;

@ConfigurationProperties(prefix = "custom.redisson")
public class DynamicRedissonProperties {

    List<String> sites;
    List<SiteRedissonProperties> list; 

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

	public List<SiteRedissonProperties> getList() {
        return list;
    }

    public void setList(List<SiteRedissonProperties> list) {
        this.list = list;
    }

}
