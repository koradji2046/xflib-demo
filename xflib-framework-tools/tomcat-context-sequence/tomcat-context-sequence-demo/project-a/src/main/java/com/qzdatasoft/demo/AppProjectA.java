package com.qzdatasoft.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class AppProjectA {

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(AppProjectA.class);
		
        springApplication.addListeners(new ApplicationListener<ApplicationStartingEvent>(){

			@Override
			public void onApplicationEvent(ApplicationStartingEvent arg0) {
				System.out.println("project-a starting .....");
			}
        	
        });

        springApplication.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
            System.out.println("project-a is ready");
        });

        springApplication.run(args);

	}
	
	

}
