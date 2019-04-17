/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.feign.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xflib.demo.configuration.feign.service.EnableXfLibImportSelector;
import com.xflib.demo.feign.service.service.XflibService;

/**
 * @author koradji
 * @date 2019/1/27
 */
@SpringBootApplication
//@EnableXfLibImportSelector
@ComponentScan(basePackages = { "com.xflib.*.configuration", })
@RestController
public class Startup {

	private static Logger log = LoggerFactory.getLogger(Startup.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Startup.class, args);
	}

	// 自动注入 XflibService
	@Autowired
	private XflibService service;

	@RequestMapping("/test")
	public String testTuling() {
		service.testService();
		log.info("==>ok");
		return "Ok";
	}
	
	@RequestMapping("/test2")
	public String testTuling2() {
		service.testService();
		log.info("==>ok2");
		return "Ok2";
	}
}
