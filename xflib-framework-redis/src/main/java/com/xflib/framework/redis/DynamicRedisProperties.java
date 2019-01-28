/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import java.util.List;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author koradji
 * @date 2019/1/27
 */
@ConfigurationProperties(prefix = "custom.redis")
public class DynamicRedisProperties{
	List<RedisPropertiesEx> list; 

	public List<RedisPropertiesEx> getList() {
        return list;
    }

    public void setList(List<RedisPropertiesEx> list) {
        this.list = list;
    }
    
    @JsonIgnore
    public RedisProperties getProperties(String key){
        RedisProperties result=null;
        for(int i=0;i<list.size();i++){
            if (list.get(i).getSite().equals(key)){
                result=list.get(i).getConfig();
                break;
            }
        };
        return result;
    }

}
