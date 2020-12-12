package com.example.ajiranet.model;

public enum CommandTypeEnum {
	
	
	
	CREATE("CREATE"),FETCH("FETCH"),MODIFY("MODIFY");
	
	
	private String commandType;
	
	CommandTypeEnum(String commandType){
		this.commandType = commandType;
	}
	
	public String getValue() {
		return commandType;
	}
	

}
