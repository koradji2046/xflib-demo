/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.amqp;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteRabbitProperties {

    private String site;
    private RabbitProperties config;

    public RabbitProperties getConfig() {
        return config;
    }

    public void setConfig(RabbitProperties config) {
        this.config = config;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


}
