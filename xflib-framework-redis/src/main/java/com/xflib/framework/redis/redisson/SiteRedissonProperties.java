/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.redisson;

import java.util.List;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteRedissonProperties {

    private String site;
    private List<SiteSourceRedissonProperties> sources;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<SiteSourceRedissonProperties> getSources() {
        return sources;
    }

    public void setSources(List<SiteSourceRedissonProperties> sources) {
        this.sources = sources;
    }

}
