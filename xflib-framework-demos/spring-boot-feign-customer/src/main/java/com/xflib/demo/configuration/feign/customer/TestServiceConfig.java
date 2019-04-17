package com.xflib.demo.configuration.feign.customer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.xflib.demo.feign.sdk.XflibService;

@Configuration
@ComponentScan(basePackages={"com.xflib.demo.feign.customer.service"})
public class TestServiceConfig {
}
