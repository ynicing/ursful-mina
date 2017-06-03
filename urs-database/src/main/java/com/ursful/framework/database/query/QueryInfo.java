package com.ursful.framework.database.query;

import com.ursful.framework.database.page.Page;
import com.ursful.framework.database.page.Pair;

import java.util.List;

public class QueryInfo {
	//private QueryType type;
	
	private String sql;
	private Class<?> clazz;//bean? String?
	Column column;//count? 
	private List<Column> columns;
	private List<Pair<Object>> values;
	private Page page;
	
	public Column getColumn() {
		return column;
	}
	public void setColumn(Column column) {
		this.column = column;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public List<Pair<Object>> getValues() {
		return values;
	}
	public void setValues(List<Pair<Object>> values) {
		this.values = values;
	}
	
	
	
}
