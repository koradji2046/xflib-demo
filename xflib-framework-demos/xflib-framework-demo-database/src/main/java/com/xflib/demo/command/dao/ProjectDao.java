package com.xflib.demo.command.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.xflib.demo.command.pojo.Project;

@Repository
public interface ProjectDao extends CrudRepository<Project, String>{

}
