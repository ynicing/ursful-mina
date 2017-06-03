package com.ursful.framework.database.page;

import java.io.Serializable;

public class Pair<T> implements Serializable{
	 
	private static final long serialVersionUID = 617587755877055322L;
	
	private String name;
    private String type;
	private T value;
	
	public Pair(String name, String type, T value){
		this.name = name;
        this.type = type;
		this.value = value;
	}

    public Pair(String name, T value){
        this.name = name;
        this.value = value;
        if(value != null){
            this.type = value.getClass().getSimpleName();
        }
    }
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
		if(name == null){
			return value + "";
		}
		return "(" + name + "," + value + ")";
	}
	
}
