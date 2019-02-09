/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.database;

import java.util.List;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class SiteSourceDataSourceProperties {

    private String source;
    private DataSourceProperties config;
    private JpaProperties jpa;
    private List<String> packages;

    public DataSourceProperties getConfig() {
        return config;
    }

    public void setConfig(DataSourceProperties config) {
        this.config = config;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JpaProperties getJpa() {
        return jpa;
    }

    public void setJpa(JpaProperties jpa) {
        this.jpa = jpa;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
}
