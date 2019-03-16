package com.xflib.demo.command.service;

import java.util.List;

import com.xflib.demo.command.pojo.Project;

public interface ProjectService {
	
	List<Project> insert(List<Project> projects);
		
	List<Project> update(List<Project> projects);

	void delete(List<Project> projects);

}
