package com.ursful.framework.database;

import com.ursful.framework.database.page.Pair;

import java.util.List;

public class SQLHelper {
	
	private String sql;
	private List<Pair<Object>> parameters;
	
 
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<Pair<Object>> getParameters() {
		return parameters;
	}
	public void setParameters(List<Pair<Object>> parameters) {
		this.parameters = parameters;
	}
	
	public String toString(){
		return sql + " : " + parameters;
	}
	
	
}
