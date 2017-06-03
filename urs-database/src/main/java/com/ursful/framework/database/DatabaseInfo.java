package com.ursful.framework.database;

public class DatabaseInfo {
	
	private String driver;
	private String url;
	private String username;
	private String password;
	private Integer maxActive = 30;
	private Integer minActive = 5;
	private Integer timeout = 60;//s
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	public Integer getMinActive() {
		return minActive;
	}
	public void setMinActive(Integer minActive) {
		this.minActive = minActive;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	
	
	 
}
