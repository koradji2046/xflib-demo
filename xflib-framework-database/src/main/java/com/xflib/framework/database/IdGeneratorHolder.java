/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.database;

/**
 * @author koradji
 * @date 2019/1/27
 */
public abstract class IdGeneratorHolder {

    private static InheritableThreadLocal<String> idPrefixContextHolder = 
            new InheritableThreadLocal<String>(); 

    public static void setPrefix(String idPrefix) {
        idPrefixContextHolder.set(idPrefix);
    }

    public static String getPrefix() {
        String site = idPrefixContextHolder.get();
        return (site == null || site.isEmpty()) ? "default" : site;
    }

}
