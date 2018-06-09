package com.qzdatasoft.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class AppProjectB {

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(AppProjectB.class);
		
        springApplication.addListeners((ApplicationListener<ApplicationStartingEvent>) event -> {
				System.out.println("project-b starting .....");
        });

        springApplication.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
            System.out.println("project-b is ready");
        });

        springApplication.run(args);

	}
	
	

}
