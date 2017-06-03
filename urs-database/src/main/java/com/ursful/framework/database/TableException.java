package com.ursful.framework.database;

public class TableException extends Exception {
 
	private static final long serialVersionUID = 3702807659429166051L;
	
	private String message;
	 
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TableException(String message){
		super(message);
		this.message = message;
	}
}
