/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.naming.factory.DataSourceLinkFactory.DataSourceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.xflib.demo.command.pojo.Project;
import com.xflib.demo.command.service.ProjectService;
import com.xflib.framework.database.DynamicDataSourceHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DatabaseCommandLineService{

    @Autowired
    private ProjectService service;

    public DatabaseCommandLineService() {
    }

    public void test(String id) {
    	List<Project> data=new ArrayList<>();
    	data.add(entity(id));
    	service.insert(data);
//    	service.delete(data);
    }

    private Project entity(String id){
    	Project project = new Project();
    	project.setId(id);
    	project.setCode(id);
    	project.setName(id+"_Name");
    	project.setDescription(id+"_Description");
    	
    	return project;
    }
}
