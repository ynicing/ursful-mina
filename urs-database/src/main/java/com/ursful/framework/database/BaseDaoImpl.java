/*
 * Copyright 2017 @ursful.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ursful.framework.database;


import com.ursful.framework.database.annotaion.RdColumn;
import com.ursful.framework.database.page.Page;
import com.ursful.framework.database.query.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDaoImpl<T>  implements IBaseDao<T>{

    private static final int ERROR_QUERY_TABLE = 101;

	@Override
	public boolean save(T t) {
		PreparedStatement ps = null;
		try {
			SQLHelper helper = SQLHelperCreator.save(t);

            System.out.println("help:" + helper.getSql());

			Connection conn = ConnectionManager.getManager().getConnection();
			ps = conn.prepareStatement(helper.getSql());
			SQLHelperCreator.setParameter(ps, helper.getParameters());
			return ps.executeUpdate() > 0;
		} catch (TableException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			ConnectionManager.getManager().close(ps, null);
		}
	}

	@Override
	public boolean update(T t) {
		return update(t, false);
	}

    @Override
    public boolean update(T t, boolean updateNull) {
        PreparedStatement ps = null;
        try {
            SQLHelper helper = SQLHelperCreator.update(t, updateNull);
            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            return ps.executeUpdate() > 0;
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, null);
        }
    }

	@Override
	public boolean delete(T t) {
		PreparedStatement ps = null;
		try {
			SQLHelper helper = SQLHelperCreator.delete(t);
			Connection conn = ConnectionManager.getManager().getConnection();
			ps = conn.prepareStatement(helper.getSql());
			SQLHelperCreator.setParameter(ps, helper.getParameters());
			return ps.executeUpdate() > 0;
		} catch (TableException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			ConnectionManager.getManager().close(ps, null);
		}
	}

	@SuppressWarnings("unchecked")
	public T get(T t) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		T temp = null;
		try {
			SQLHelper helper = SQLHelperCreator.get(t);
			Connection conn = ConnectionManager.getManager().getConnection();
			ps = conn.prepareStatement(helper.getSql());
			SQLHelperCreator.setParameter(ps, helper.getParameters());
			rs = ps.executeQuery();
			if (rs.next()) {
				temp = (T)t.getClass().newInstance();
				SQLHelperCreator.newClass(t.getClass(), rs, temp);
			}
		} catch (TableException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} finally{
			ConnectionManager.getManager().close(ps, rs);
		}
		return temp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> query(IQuery<T> q) throws QueryException{
		
		Connection conn = ConnectionManager.getManager().getConnection();
		//setClob通用
		//private List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		QueryInfo qinfo = q.getQueryInfo();
		
		List<T> temp = new ArrayList<T>();
		
		String query = qinfo.getSql();// = queryString(names, false);
		
		Class<?> [] cc  = null;
		int size = qinfo.getColumns().size();
		cc = new Class<?>[size];
		for(int i = 0; i < size; i++){
			cc[i] = QueryUtil.getFieldClass(qinfo.getColumns().get(i), qinfo.getClazz());
		}
		
		Constructor<?> c = null;
		if(cc != null){
			try {
				c = qinfo.getClazz().getConstructor(cc);
			} catch (Exception e) {
				c = null;
			}
		}

        System.out.println("query:" + query);
        System.out.println("paras:" + qinfo.getValues());
        //LogUtil.info("query:" + query, BaseDaoImpl.class);
		//LogUtil.info("paras:" + qinfo.getValues(), BaseDaoImpl.class);
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			ps = conn.prepareStatement(query);
			SQLHelperCreator.setParameter(ps, qinfo.getValues());
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(c == null){
					T t = (T)qinfo.getClazz().newInstance();
					SQLHelperCreator.newClass(qinfo.getClazz(), rs, t);
					for(int i = 0; i < size; i++){
						Column colu = qinfo.getColumns().get(i);//如果是别名，＋
						if(colu.getAsName() != null){
 							Field field = QueryUtil.getField(colu, qinfo.getClazz());
							Object fo = SQLHelperCreator.getFieldObject(rs, field, colu.getAsName());
							field.setAccessible(true);
							if(fo != null){
								field.set(t, fo);
							}
						}
					}
					temp.add(t);
				}else{
					Object [] objs = new Object[size];
					int j = 0;
					for(int i = 0; i < size; i++){
						Column colu = qinfo.getColumns().get(i);//如果是别名，＋
						if(colu.getAsName() != null){
							Field field = QueryUtil.getField(colu, qinfo.getClazz());
							Object fo = SQLHelperCreator.getFieldObject(rs, field, colu.getAsName());
							objs[j] = fo;
						}else{
							Field field = QueryUtil.getField(colu, qinfo.getClazz());
							Object fo = SQLHelperCreator.getFieldObject(rs, 
								field, 
								((RdColumn)field.getAnnotation(RdColumn.class)).name());
							objs[j] = fo;
						}
						
					}
					T t = (T)c.newInstance(objs);
					temp.add(t);
				}
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally{
			ConnectionManager.getManager().close(ps, rs);
		}
		return temp;
	}

	@Override
	public int queryCount(IQuery<T> q) throws QueryException{
		//setClob通用
		//private List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		int count = 0;	
		QueryInfo qinfo = q.getQueryInfo();
		
		String query = qinfo.getSql();// = queryString(names, false);
		 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = ConnectionManager.getManager().getConnection();
			ps = conn.prepareStatement(query);
			SQLHelperCreator.setParameter(ps, qinfo.getValues());
			rs = ps.executeQuery();
			
			if(rs.next()){
				 count = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} finally{
			ConnectionManager.getManager().close(ps, rs);
		}
		return count;
	}

	@Override
	public Page queryPage(IQuery<T> query) throws QueryException{
		Page page = query.getQueryInfo().getPage();
		if(page == null){
			page = new Page();
		}
		query.createPage(page);
		List<T> list = query(query);
		query.createCount();
		int total = queryCount(query);
		page.setRows(list);
		page.setTotal(total);
		return page;
	}

	@Override
	public List<String> queryDistinctString(IQuery<T> q)
			throws QueryException {
		List<String> tmp = new ArrayList<String>();	
		QueryInfo qinfo = q.getQueryInfo();
		
		String query = qinfo.getSql();// = queryString(names, false);
		 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = ConnectionManager.getManager().getConnection();
			ps = conn.prepareStatement(query);
			SQLHelperCreator.setParameter(ps, qinfo.getValues());
			rs = ps.executeQuery();
			while(rs.next()){
				 tmp.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + query + "]" + e.getMessage());
		} finally{
			ConnectionManager.getManager().close(ps, rs);
		}
		return tmp;
	}

	
	
	/*
	public static <T> T rsMapEntity(Class<T> clazz, ResultSet rs) {
		ResultSetMetaData rsmd = null;
		String temp = "";
		Method s = null;
		T t = null;
		try {
			rsmd = rs.getMetaData();
			if (rs.next()) {
				t = clazz.newInstance();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					temp = rsmd.getColumnName(i);
					s = clazz.getDeclaredMethod(StringHelper
							.asserSetMethodName(StringHelper
									.toJavaAttributeName(temp)), String.class);
					s.invoke(t, rs.getString(temp));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return t;
	}


	public static <T> List<T> rsMapToEntityList(Class<T> clazz,
			ResultSet rs) {
		ResultSetMetaData rsmd = null;
		List<T> list = new ArrayList<T>();
		String temp = "";
		Method s = null;
		T t = null;
		try {
			rsmd = rs.getMetaData();
			while (rs.next()) {
				t = clazz.newInstance();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					temp = rsmd.getColumnName(i);
					s = clazz.getDeclaredMethod(StringHelper
							.asserSetMethodName(StringHelper
									.toJavaAttributeName(temp)), String.class);
					s.invoke(t, rs.getString(temp));
				}
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return list;
	}*/
}
