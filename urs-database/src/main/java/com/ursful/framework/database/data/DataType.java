package com.ursful.framework.database.data;


public enum DataType {
	/**
	 * 默认都是String，因为是Http请求！
	 */
	VOID("v", "V", "void", "VOID"),
	BOOLEAN("z","Z","boolean", "BOOLEAN", "java.lang.Boolean"),
	CHAR("c","C","char", "Character", "java.lang.Character"),
	BYTE("b","B","byte", "Byte", "java.lang.Byte"),
	SHORT("s","S","short", "Short", "java.lang.Short"),
	INTEGER("i","I","int", "Integer", "java.lang.Integer"),
	FLOAT("f","F","float", "Float", "java.lang.Float"),
	LONG("j","J","long", "Long", "java.lang.Long"),
	DOUBLE("d","D","double","Double","java.lang.Double"),
	DATE("Date","java.util.Date"),
	TIMESTAMP("Timestamp","java.sql.Timestamp"),
	STRING("String", "java.lang.String"),
	OBJECT("Object","java.lang.Object"),
	UNKNOWN("UNKOWN");
//	Collection
//	├List
//	│├LinkedList
//	│├ArrayList
//	│└Vector
//	│　└Stack
//	└Set
	
//	集合只支持[],List,Map
//  List
//	Map
//  []
	
	//UNKNOWN("UNKNOWN", "unknown"),
	//,OBJECT("o","obj","object", "Object","java.lang.Object");
	
	private String [] types;
	 
	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	DataType(String... types){
		this.types = types;
	}
  
	public static DataType getDataType(String type){
		DataType result = UNKNOWN;
		first:for(DataType dt : DataType.values()){
			String[] types = dt.getTypes();
			for(String t : types){
				if(t.equals(type)){
					result = dt;
					break first;
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
	}
	

}
