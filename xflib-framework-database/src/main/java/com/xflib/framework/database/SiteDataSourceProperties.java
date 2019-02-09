/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.database;

import java.util.List;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteDataSourceProperties {

    private String site;
    private List<SiteSourceDataSourceProperties> sources;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<SiteSourceDataSourceProperties> getSources() {
        return sources;
    }

    public void setSources(List<SiteSourceDataSourceProperties> sources) {
        this.sources = sources;
    }

}
