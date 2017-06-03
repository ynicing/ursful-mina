package com.ursful.framework.database;


import com.ursful.framework.database.data.DataType;

public class ColumnInfo {
	
	private String name;//test.userId
	private String columnName;//user_id
	private DataType dataType;//String
	private Boolean unique;//pk?
	private String largeString;//clob?
	
	public String getLargeString() {
		return largeString;
	}
	public void setLargeString(String largeString) {
		this.largeString = largeString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public Boolean getUnique() {
		return unique;
	}
	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public String toString(){
		return name + "(" + columnName + ",pk:" + unique + ")" + (dataType != null? dataType.name():"null");
	}
}
