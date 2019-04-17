package com.xflib.demo.feign.sdk;

import org.springframework.context.annotation.Bean;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class XflibServiceConfig {

	@Bean
	public RequestInterceptor interceptor() {
		return new RequestInterceptor() {

			@Override
			public void apply(RequestTemplate arg0) {
				// TODO Auto-generated method stub
				System.out.println("XflibServiceConfig");
			}

		};

	}

}
