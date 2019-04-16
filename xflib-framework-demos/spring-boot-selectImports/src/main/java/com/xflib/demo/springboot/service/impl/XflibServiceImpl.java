package com.xflib.demo.springboot.service.impl;

import com.xflib.demo.springboot.service.XflibService;

public class XflibServiceImpl implements XflibService {

	@Override
	public void testService() {
		System.out.println("==>我是通过importSelector导入进来的service");
	}

}
