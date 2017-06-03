package com.ursful.framework.database.query;

public class QueryException extends Exception {
 
	private static final long serialVersionUID = 3702807659429166051L;
	
	private Integer code;
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	private String message;
	 
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public QueryException(Integer code, String message){
		super(message);
		this.code = code;
		this.message = message;
	}
}
