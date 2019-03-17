/**Copyright: Copyright (c) 2016, Koradji */
package com.xflib.framework.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态数据源注解<br>
 * <br>
 * History:<br> 
 *    . v1.0.0.20161102, Koradji, Create<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSource {
    String value();
}
