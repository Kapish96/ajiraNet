package com.example.ajiranet.model;

import lombok.Builder;

@Builder
public class DeviceVO {

	String name;
	String type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public DeviceVO() {
		// TODO Auto-generated constructor stub
	}
	public DeviceVO(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	
}
