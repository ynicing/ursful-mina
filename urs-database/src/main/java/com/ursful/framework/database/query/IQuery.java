package com.ursful.framework.database.query;



import com.ursful.framework.database.page.Page;


public interface IQuery<T> {
	IQuery<T> createDistinctQuery(Class<?> clazz, Column... columns) throws QueryException;
	IQuery<T> createQuery(Class<?> clazz, Column... columns) throws QueryException;//select a.id, a.name from
	IQuery<T> createCount() throws QueryException;;//select count(*) from.
	IQuery<T> createCount(Column column) throws QueryException;//select count(a.id) from...
	IQuery<T> createDistinctString(Column column) throws QueryException;
	IQuery<T> createPage(Page page) throws QueryException;// page...
	QueryInfo getQueryInfo();
	//由createQuery决定
	//List<T> query() throws QueryException;
	//int queryCount() throws QueryException;
	//Page queryPage(Page page) throws QueryException;
	
	//query group, query distinct
	//query other?
}
