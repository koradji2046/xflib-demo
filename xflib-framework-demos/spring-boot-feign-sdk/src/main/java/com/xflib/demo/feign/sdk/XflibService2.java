package com.xflib.demo.feign.sdk;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="xflibService2",url="http://127.0.0.1:8080",configuration=XflibServiceConfig2.class)
public interface XflibService2 {
	
	@RequestMapping("/test2")
	public String test();
}
