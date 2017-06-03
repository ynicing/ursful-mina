package com.ursful.framework.database;

import java.util.Map;

public class TableInfo implements Cloneable{
	
	private String name;//Test
	private String tableName;//t_test
	private String aliasName;//t
	private Class<?> clazz;//Test.class
	private Map<String, ColumnInfo> columns;
	private Integer order;
	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, ColumnInfo> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, ColumnInfo> columns) {
		this.columns = columns;
	}
	
	public String toString(){
		return name + "(" + tableName + ")" + columns;
	}
	
	public TableInfo clone(){
        TableInfo o = null;
        try{
            o = (TableInfo)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }
}
