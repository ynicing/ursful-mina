package com.ursful.framework.database.query;
public class Column{
	
	private String name;
	private String function;
	private String alias;
	private String asName;
	
	public String getAsName() {
		return asName;
	}

	public void setAsName(String asName) {
		this.asName = asName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	//---num
	//count(num)
	//count(num) as n
	//x count(a.num)
	//--a.num
	//--a.num as n
	//--count(a.num) as n
	
	//count(num) as n
	/*public Column(StringBuffer function, String name, String asName){
		this.function = function.toString();
		this.name = name;
		this.asName = asName;
	}*/
 
	//num
	public Column(String name){
		this.name = name;
	}
		
	//u.num
	public Column(String alias, String name){
		this.name = name;
		this.alias = alias;
	}
		
	//u.num as big
	public Column(String alias, String name, String asName){
		this.alias = alias;
		this.name = name;
		this.asName = asName;
	}
	
	//sum(u.num) as big
	public Column(String function, String alias, String name, String asName){
		this.function = function;
		this.alias = alias;
		this.name = name;
		this.asName = asName;
	}

}