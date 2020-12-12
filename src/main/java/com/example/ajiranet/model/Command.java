package com.example.ajiranet.model;

public enum Command {
	

	DEVICES("devices"), CONNECT_DEVICES("connections");
	
	
	private String command;
	
	Command(String command){
		this.command = command;
	}
	
	public String getValue() {
		return command;
	}

}
