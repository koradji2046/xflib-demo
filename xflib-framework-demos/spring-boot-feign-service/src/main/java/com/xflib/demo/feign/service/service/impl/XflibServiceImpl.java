package com.xflib.demo.feign.service.service.impl;

import com.xflib.demo.feign.service.service.XflibService;

public class XflibServiceImpl implements XflibService {

	@Override
	public void testService() {
		System.out.println("==>test service");
	}

}
