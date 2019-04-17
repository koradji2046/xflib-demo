package com.xflib.demo.feign.customer.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xflib.demo.feign.sdk.XflibService;
import com.xflib.demo.feign.sdk.XflibService2;

@Service
public class TestService {

	
	@Autowired
	XflibService service;
	
	@Autowired
	XflibService2 service2;
	
	@PostConstruct
	void init(){
		service.test();
		service2.test();
	}
}
