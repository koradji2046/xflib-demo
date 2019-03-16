package com.xflib.demo.command.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xflib.demo.command.dao.ProjectDao;
import com.xflib.demo.command.pojo.Project;
import com.xflib.demo.command.service.ProjectService;

@Service
@Transactional
//@Scope("prototype")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectDao dao;
	
	@Override
	public List<Project> insert(List<Project> projects){
		return (List<Project>) dao.save(projects);
	}
		
	@Override
	public List<Project> update(List<Project> projects){
		return (List<Project>) dao.save(projects);
	}

	@Override
	public void delete(List<Project> projects){
		dao.delete(projects);
	}

}
