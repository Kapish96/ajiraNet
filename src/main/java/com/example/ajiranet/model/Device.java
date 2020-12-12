package com.example.ajiranet.model;

import java.util.List;

public class Device {
	
	
	private String type;
	private String name;
	private List<String> connections;
	
	private long strength;
	
	public Device() {
		super();
	}
	public Device(String type, String name, List<String> connections, long strength) {
		super();
		this.type = type;
		this.name = name;
		this.connections = connections;
		this.strength = strength;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getConnections() {
		return connections;
	}
	public void setConnections(List<String> connections) {
		this.connections = connections;
	}
	public long getStrength() {
		return strength;
	}
	public void setStrength(long strength) {
		this.strength = strength;
	}
	
	@Override
	public String toString() {
		return "Device [type=" + type + ", name=" + name + ", connections=" + connections + ", strength=" + strength
				+ "]";
	}

}
