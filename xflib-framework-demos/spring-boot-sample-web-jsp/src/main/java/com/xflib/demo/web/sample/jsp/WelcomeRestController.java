package com.xflib.demo.web.sample.jsp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class WelcomeRestController {

	@RequestMapping("/hi")
	public UserInfo sayHi(String name){
		UserInfo uo=new UserInfo(name);
		return uo;
	}
	
class UserInfo{
	private String name;
	private int age;
	
	public UserInfo(String name){
		this(name,0);
	}
	public UserInfo(String name, int age){
		this.name=name;
		this.age=age;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
}
