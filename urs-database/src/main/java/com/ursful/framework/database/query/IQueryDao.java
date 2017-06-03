package com.ursful.framework.database.query;

public interface IQueryDao<T> extends IQuery<T>{
	 
	//从class中获取字段，该字段可有可无
	IQueryDao<T> table(Class<?> clazz) throws QueryException;
	IQueryDao<T> where(String name, Object value, ConditionType type);
	IQueryDao<T> group(String name);
	IQueryDao<T> having(String name, Object value, ConditionType type);
	IQueryDao<T> orderDesc(String name);
	IQueryDao<T> orderAsc(String name);
	IQueryDao<T> createQuery(String... names) throws QueryException;
	IQueryDao<T> createQuery(Column... columns) throws QueryException;
	
	
}
