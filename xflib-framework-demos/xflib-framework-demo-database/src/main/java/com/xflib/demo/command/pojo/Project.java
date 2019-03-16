package com.xflib.demo.command.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_project")
public class Project implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 14)
	private String id;
	@Column(name = "code", length = 20)
	private String code;
	@Column(name = "name", length = 60)
	private String name;
	@Column(name = "description", length = 100)
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
