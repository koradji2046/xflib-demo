/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.redis;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xflib.framework.redis.SiteRedisProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
@ConfigurationProperties(prefix = "custom.redis")
public class DynamicRedisProperties{
    List<String> sites;
    List<SiteRedisProperties> list; 

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

	public List<SiteRedisProperties> getList() {
        return list;
    }

    public void setList(List<SiteRedisProperties> list) {
        this.list = list;
    }
    
//    @JsonIgnore
//    public RedisProperties getProperties(String key){
//        RedisProperties result=null;
//        for(int i=0;i<list.size();i++){
//            if (list.get(i).getSite().equals(key)){
//                result=list.get(i).getConfig();
//                break;
//            }
//        };
//        return result;
//    }

}
