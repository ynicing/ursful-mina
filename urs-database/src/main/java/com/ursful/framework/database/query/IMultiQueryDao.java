package com.ursful.framework.database.query;

public interface IMultiQueryDao<T> extends IQuery<T>{
	 
	IMultiQueryDao<T> createAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> createLeftAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> createRightAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> group(Column column);
	IMultiQueryDao<T> having(Column left, Object value, ConditionType type);
	IMultiQueryDao<T> on(Column left, Column right);
	IMultiQueryDao<T> where(Column left, Object value, ConditionType type);
	IMultiQueryDao<T> where(Column left, Column value);
	IMultiQueryDao<T> orderDesc(Column column);
	IMultiQueryDao<T> orderAsc(Column column);
	
}
