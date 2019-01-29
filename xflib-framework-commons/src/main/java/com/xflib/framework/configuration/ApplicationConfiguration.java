/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.framework.utils.SpringUtils;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

}
