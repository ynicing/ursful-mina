package com.ursful.framework.database;

import com.ursful.framework.database.page.Page;
import com.ursful.framework.database.query.IQuery;
import com.ursful.framework.database.query.QueryException;

import java.util.List;


public interface IBaseDao<T>{
    T get(T t);
	boolean save(T t);
	boolean update(T t);
    boolean update(T t, boolean updateNull);
	boolean delete(T t);
	List<T> query(IQuery<T> query) throws QueryException;
	int queryCount(IQuery<T> query) throws QueryException;
	Page queryPage(IQuery<T> query) throws QueryException;
	List<String> queryDistinctString(IQuery<T> query) throws QueryException;
}
