/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.database;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.xflib.framework.database.SiteDataSourceProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
@ConfigurationProperties(prefix = "custom.datasource")
public class DynamicDataSourceProperties{
    private List<String> sites;
    private List<SiteDataSourceProperties> list; 

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

	public List<SiteDataSourceProperties> getList() {
        return list;
    }

    public void setList(List<SiteDataSourceProperties> list) {
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
