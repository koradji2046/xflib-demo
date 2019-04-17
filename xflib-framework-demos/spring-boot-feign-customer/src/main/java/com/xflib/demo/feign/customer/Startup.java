/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.feign.customer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import com.xflib.demo.feign.sdk.XflibService;

/**
 * @author koradji
 * @date 2019/1/27
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.xflib.*.configuration" })
@EnableFeignClients(basePackages={"com.xflib.*.feign.sdk"})
public class Startup {

	private static Logger log = LoggerFactory.getLogger(Startup.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Startup.class, args);
	}
	
}
