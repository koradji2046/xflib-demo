/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.xflib.framework.database.DynamicDataSourceHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DatabaseCommandLineRunner implements CommandLineRunner {

    @Autowired
    private DatabaseCommandLineService service;

    public DatabaseCommandLineRunner() {
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        DynamicDataSourceHolder.setSite("30001");
        service.test("2");

        System.out.println("=> 上线前请清除这个测试类: " + this.getClass().getName());
    }
}
