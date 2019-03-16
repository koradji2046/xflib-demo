/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.xflib.demo.command.DatabaseCommandLineRunner;
import com.xflib.demo.command.DatabaseCommandLineService;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@EntityScan(basePackages={"com.xflib.demo.command.pojo"})
@EnableJpaRepositories(basePackages={"com.xflib.demo.command.dao"})
@ComponentScan(basePackages={
		"com.xflib.demo.command.controller",
		"com.xflib.demo.command.service.impl",
		})
@ConditionalOnProperty(prefix="demo.enabled",name="databaseCommandLineRunner",havingValue="true",matchIfMissing=false)
public class DatabaseConfiguration {

    @Bean
    public DatabaseCommandLineService databaseCommandLineService() {
        return new DatabaseCommandLineService();
    }
    
    @Bean
    public DatabaseCommandLineRunner DatabaseCommandLineRunner() {
        return new DatabaseCommandLineRunner();
    }

}
