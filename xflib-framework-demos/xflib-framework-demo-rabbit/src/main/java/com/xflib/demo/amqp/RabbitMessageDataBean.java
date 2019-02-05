package com.xflib.demo.amqp;

import java.util.List;

public class RabbitMessageDataBean {

    public RabbitMessageDataBean() {}
    
    public RabbitMessageDataBean(List<String> data, String site, Integer id) {
        super();
        this.data = data;
        this.site = site;
        this.id = id;
    }

    private List<String> data;
	private String site;
	private Integer id;

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
