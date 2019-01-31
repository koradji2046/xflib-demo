/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.amqp;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
@ConfigurationProperties(prefix = "custom.rabbitmq")
public class DynamicRabbitProperties{
    List<String> sites;
    List<SiteRabbitProperties> list; 

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

	public List<SiteRabbitProperties> getList() {
        return list;
    }

    public void setList(List<SiteRabbitProperties> list) {
        this.list = list;
    }

}
