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
package com.weitu.framework.component.orm;


import com.weitu.framework.component.orm.helper.SQLHelperCreator;
import com.weitu.framework.component.orm.query.QueryUtils;
import com.weitu.framework.component.orm.*;
import com.weitu.framework.component.orm.exception.QueryException;
import com.weitu.framework.component.orm.exception.TableException;
import com.weitu.framework.component.orm.support.*;
import com.weitu.framework.component.orm.helper.SQLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseServiceImpl<T> implements IBaseService<T>{

    private static final int ERROR_QUERY_TABLE = 101;

    @Autowired
    private DataSource dataSource;

    private Class<?> thisClass;

    protected BaseServiceImpl() {
        Type[] ts = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        thisClass = (Class<T>) ts[0];
    }

    private Connection getConnection(){
        return Manager.getManager().getConnection(dataSource);
    }

    private void close(ResultSet rs, Statement statement, Connection connection){
        Manager.getManager().close(rs, statement, connection, dataSource);
    }
	
	public boolean save(T t) {
		PreparedStatement ps = null;
        Connection conn = null;
		try {
			SQLHelper helper = SQLHelperCreator.save(t);

            System.out.println("SAVE : " + helper);

            conn = getConnection();
            if(helper.getIdField() != null) {
                ps = conn.prepareStatement(helper.getSql(), Statement.RETURN_GENERATED_KEYS);
            }else{
                ps = conn.prepareStatement(helper.getSql());
            }
            //ps = conn.prepareStatement(sql);
            //ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //ps = conn.prepareStatement(sql, new String[]{idCols.getFirst()});

			SQLHelperCreator.setParameter(ps, helper.getParameters());
			boolean flag =  ps.executeUpdate() > 0;

            try {
                if(helper.getIdField() != null) {
                    ResultSet seqRs = ps.getGeneratedKeys();
                    seqRs.next();
                    Object key = seqRs.getObject(1);
                    helper.setId(t, key);
                    seqRs.close();
                }
            }catch (Exception e){
                System.out.println("not support.");
            }

            return flag;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (TableException e) {
            throw new RuntimeException(e);
        } finally{
			close(null, ps, conn);
		}
	}

	
	public boolean update(T t) {
		return update(t, false);
	}

    
    public boolean update(T t, boolean updateNull) {
        PreparedStatement ps = null;

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.update(t, updateNull);

            System.out.println("UPDATE : " + helper);

            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            return ps.executeUpdate() > 0;
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(null, ps, conn);
        }
    }

	
	public boolean delete(Object t) {
		PreparedStatement ps = null;

        Connection conn = null;
		try {
			SQLHelper helper = null;
            if(thisClass.isInstance(t)){
                helper = SQLHelperCreator.delete(t);
            }else{
                helper = SQLHelperCreator.delete(thisClass, t);
            }

            System.out.println("DELETE : " + helper);

			conn = getConnection();
			ps = conn.prepareStatement(helper.getSql());
			SQLHelperCreator.setParameter(ps, helper.getParameters());
			return ps.executeUpdate() > 0;
        } catch (TableException e) {
            throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally{
			close(null, ps, conn);
		}
	}


    public List<T> list() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> temp = new ArrayList<T>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.query(thisClass, null, null, null, null, null);

            System.out.println("list() : " + helper);

            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            rs = ps.executeQuery();
            while (rs.next()) {
                T tmp = SQLHelperCreator.newClass(thisClass, rs);
                temp.add(tmp);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }

    public List<T> list(int start, int size) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> temp = new ArrayList<T>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.query(thisClass, null, null, null, start, size);
            System.out.println("list(start,size) : " + helper);
            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            while (rs.next()) {
                T tmp = SQLHelperCreator.newClass(thisClass, rs);
                temp.add(tmp);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }

    public List<T> list(Express ... expresses) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> temp = new ArrayList<T>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.query(thisClass, expresses);

            System.out.println("list(expresses) : " + helper);

            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            while (rs.next()) {
                T tmp = SQLHelperCreator.newClass(thisClass, rs);
                temp.add(tmp);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }

    public List<T> list(Terms terms) {
        return list(terms, null);
    }

    @Override
    public List<T> list(Terms terms, MultiOrder multiOrder) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> temp = new ArrayList<T>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.query(thisClass, null, terms, multiOrder, null, null);
            System.out.println("list(terms, multiOrder) : " + helper);
            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            while (rs.next()) {
                T tmp = SQLHelperCreator.newClass(thisClass, rs);
                temp.add(tmp);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }

    public int size(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> temp = new ArrayList<String>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.queryCount(this.thisClass, null, null);
            System.out.println("SIZE : " + helper);
            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            if (rs.next()) {
                Object tmp = rs.getObject(1);
                if(tmp != null) {
                    return Integer.parseInt(tmp.toString());
                }
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return 0;
    }

    public List<KV> list(String key, String value, Terms terms, MultiOrder multiOrder){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<KV> temp = new ArrayList<KV>();

        Connection conn = null;
        try {
            SQLHelper helper = SQLHelperCreator.query(this.thisClass, new String[]{key, value}, terms, multiOrder, null, null);
            System.out.println("list(key,value,...) : " + helper);
            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            while (rs.next()) {
                KV kv = new KV();
                Object k = rs.getObject(1);
                if(k != null) {
                    kv.setKey(k.toString());
                }
                Object v = rs.getObject(2);
                if(v != null) {
                    kv.setValue(v.toString());
                }
                temp.add(kv);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }

    private <T> T getQuery(IQuery query) throws QueryException {
        List<T> list = query(query);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    
    public T get(Object t) {

        if(IQuery.class.isAssignableFrom(t.getClass())){
            return getQuery((IQuery)t);
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        T temp = null;

        Connection conn = null;
        try {
            SQLHelper helper = null;
            if(thisClass.isInstance(t)){
                helper = SQLHelperCreator.get(t);
            }else{
                helper = SQLHelperCreator.get(t, thisClass);
            }
            System.out.println("GET : " + helper);
            conn = getConnection();
            ps = conn.prepareStatement(helper.getSql());
            SQLHelperCreator.setParameter(ps, helper.getParameters());
            rs = ps.executeQuery();
            if (rs.next()) {
                temp = SQLHelperCreator.newClass(thisClass, rs);
            }
        } catch (TableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }



    public <S> List<S> query(IQuery q) throws QueryException{
        return query(q, null);
    }

	private <S> List<S> query(IQuery q, Page page) throws QueryException{
		
		Connection conn = getConnection();
		//setClob通用

		QueryHelper queryHelper = q.queryHelper();
		
		List<S> temp = new ArrayList<S>();



		QueryInfo qinfo = QueryUtils.doQuery(queryHelper, page);// = queryString(names, false);
		
        System.out.println("query:" + qinfo.getSql());
        System.out.println("paras:" + qinfo.getValues());
        //LogUtil.info("query:" + query, BaseDaoImpl.class);
		//LogUtil.info("paras:" + qinfo.getValues(), BaseDaoImpl.class);
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			ps = conn.prepareStatement(qinfo.getSql());
			SQLHelperCreator.setParameter(ps, qinfo.getValues());
			rs = ps.executeQuery();
			
			while(rs.next()){
                S t = SQLHelperCreator.newClass(qinfo.getClazz(), rs);
                temp.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new QueryException(ERROR_QUERY_TABLE, "SQL[" + qinfo.getSql() + "]" + e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally{
            close(rs, ps, conn);
		}
		return temp;
	}

	
	public int queryCount(IQuery q) throws QueryException{
		//setClob通用

        Connection conn = null;
		int count = 0;
		QueryInfo qinfo = QueryUtils.doQueryCount(q.queryHelper());
		
		String query = qinfo.getSql();// = queryString(names, false);
		 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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
			close(rs, ps, conn);
		}
		return count;
	}

	
	public <S> Page queryPage(IQuery query, Page page) throws QueryException{
		if(page == null){
			page = new Page();
		}
		List<S> list = query(query, page);
		int total = queryCount(query);
		page.setRows(list);
		page.setTotal(total);
		return page;
	}



    /*
    public List<String> getCurrentTables(){

        List<String> temp = new ArrayList<String>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        Connection conn = null;
        try {

            DatabaseType dt = getDatabaseType();

            String sql = null;
            switch(dt){
                case MYSQL:
                    //sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"+ConnectiongetDatabaseName()+"'";
                    break;
                case ORACLE:
                    sql = "select table_name from user_tables";
                    break;
                case SQLServer:
                    break;
            }

            conn = getConnection();
            ps = conn.prepareStatement(sql);
            //DBUtil.setParameter(ps, parameters);
            rs = ps.executeQuery();

            while(rs.next()){
                String tmp = rs.getString(1);
                temp.add(tmp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(conn, ps, rs);
        }
        return temp;
    }*/

    /*
    public int executeBatch(ISQLScript script, Object[] ... parameters){
        ResultSet rs = null;
        PreparedStatement ps = null;

        Connection conn = null;
        try {

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for(Object[] objects : parameters) {
                SQLHelperCreator.setParameter(ps, objects);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(conn, ps, rs);
        }
        return 0;
    }*/

//    public int execute(ISQLScript script){
//        int res = -1;
//        PreparedStatement ps = null;
//
//        Connection conn = null;
//        try {
//            conn = getConnection();
//            ps = conn.prepareStatement(sql);
//            res = ps.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally{
//            close(rs, ps, conn);
//        }
//        return res;
//    }

    public int execute(ISQLScript script, Object ... parameters){

        int res = -1;
        PreparedStatement ps = null;

        Connection conn = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(script.table());
            SQLHelperCreator.setParameter(ps, parameters);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            close(null, ps, conn);
        }
        return res;
    }



    public String queryLatestVersion(String moduleName){
        String temp = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        Connection conn = null;
        try {

            String sql = "select version from t_sys_module where name = ? order by load_date desc";
            Object [] parameters = new Object[]{moduleName};

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            conn = getConnection();
            ps = conn.prepareStatement(sql);
            SQLHelperCreator.setParameter(ps, parameters);
            rs = ps.executeQuery();
            if(rs.next()){
                temp = rs.getString(1);
            }
        } catch (SQLException e) {
            //LogUtil.warn("Not any version : " + e.getMessage(), BaseSQLImpl.class);
            temp = null;
            throw new RuntimeException(e);
        } finally{
            close(rs, ps, conn);
        }
        return temp;
    }



}
