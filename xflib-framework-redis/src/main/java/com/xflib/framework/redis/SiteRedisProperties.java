/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import java.util.List;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteRedisProperties {

    private String site;
    private List<SiteSourceRedisProperties> sources;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<SiteSourceRedisProperties> getSources() {
        return sources;
    }

    public void setSources(List<SiteSourceRedisProperties> sources) {
        this.sources = sources;
    }

}
