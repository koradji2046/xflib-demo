package com.xflib.demo.feign.sdk;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="xflibService",url="http://127.0.0.1:8080",configuration=XflibServiceConfig.class)
public interface XflibService {
	
	@RequestMapping("/test")
	public String test();
}
