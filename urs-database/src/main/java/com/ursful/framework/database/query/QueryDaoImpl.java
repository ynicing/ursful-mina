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

package com.ursful.framework.database.query;


import com.ursful.framework.database.ConnectionManager;
import com.ursful.framework.database.DatabaseType;
import com.ursful.framework.database.TableInfo;
import com.ursful.framework.database.page.Page;
import com.ursful.framework.database.page.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryDaoImpl<T> implements IQueryDao<T>{
	
	//private static IBaseDao<SysUser> baseDao = new BaseDaoImpl<SysUser>();
	
	private QueryInfo info;
	private TableInfo table;
	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Condition> havings = new ArrayList<Condition>();
	private List<Column> groups = new ArrayList<Column>();
	private List<Order> orders = new ArrayList<Order>();
	
	private boolean isDistinct = false;
	
	public IQueryDao<T> orderDesc(String name){
		orders.add(new Order(new Column(name), "DESC"));
		return this;
	}

	public IQueryDao<T> where(String name, Object value, ConditionType type){
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        if(type == ConditionType.CDT_Like){
            value = value + "%";
        }
        conditions.add(new Condition(new Column(name), value, type));
		return this;
	}
	
 
	@Override
	public IQueryDao<T> group(String name) {
		groups.add(new Column(name));
		return this;
	}

	@Override
	public IQueryDao<T> having(String name, Object value, ConditionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        if(type == ConditionType.CDT_Like){
            value = value + "%";
        }
        havings.add(new Condition(new Column(name), value, type));

		return this;
	}
	
	public IQueryDao<T> orderAsc(String name){
		orders.add(new Order(new Column(name), "ASC"));
		return this;
	}
	
	public IQueryDao<T> table(Class<?> clazz) throws QueryException{
		this.table = QueryUtil.getTableInfoFromClass(clazz);
		return this;
	}
	
	public IQueryDao<T> createQuery(String... names) throws QueryException {
		this.queryColumns = new Column[names.length];
		for(int i = 0; i < names.length; i++){
			this.queryColumns[i] = new Column(names[i]);
		}
		this.info = doQuery(this.queryColumns, null);
		return this;
	}
 
	/*
	public static void main(String[] args) throws Exception{
		
		
		Properties props = new Properties();
		props.load(QueryDaoImpl.class.getClassLoader().getResourceAsStream("deploy.properties"));
		
		DatabaseInfo dbinfo = new DatabaseInfo();
		dbinfo.setDriver(props.getProperty("database.driver"));
		dbinfo.setPassword(props.getProperty("database.password"));
		dbinfo.setUsername(props.getProperty("database.username"));
		dbinfo.setUrl(props.getProperty("database.url"));
		dbinfo.setMaxActive(Integer.parseInt(props.getProperty("database.maxActive")));
		dbinfo.setMinActive(Integer.parseInt(props.getProperty("database.maxIdle")));
		ConnectionManager.getManager().init(dbinfo);
		
		
		IQuery<SysUser> query = new QueryDaoImpl<SysUser>()
		.table(SysUser.class)
		//.where(SysUser.T_NAME, "t%", ConditionType.CDT_Like)
		.orderDesc(SysUser.T_ID)
		
		.createQuery(new Column(SysUser.T_ID), new Column(SysUser.T_NAME));
		//.createQuery(SysUser.T_ID, SysUser.T_NAME);
		
		
		
		List<SysUser> users = baseDao.query(query);
		for(SysUser user : users){
		}
		
		Page page = new Page();
		page.setSize(4);
		page.setPage(1);
		query.createPage(page);
		users = baseDao.query(query);
		for(SysUser user : users){
		}
		
		query.createCount();
		
		
		query = new QueryDaoImpl<SysUser>()
				.table(SysUser.class)
				.createQuery(new Column("distinct", null, SysUser.T_NAME, null));
				//.createQuery(SysUser.T_ID, SysUser.T_NAME);
		
		List<String> st = baseDao.queryDistinctString(query);
		
		
		query = new QueryDaoImpl<SysUser>()
				.table(SysUser.class)
				.createQuery(SysUser.T_ID, SysUser.T_NAME);
		page = baseDao.queryPage(query, page);
	}
	*/
	
	private QueryInfo doQueryCount(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
		if(column == null){
			sb.append("SELECT COUNT(*)");
		}else{
			String sn = QueryUtil.getColumnName(column, table);
			sb.append("SELECT " + sn);
		}
		 
		sb.append(" FROM ");
		sb.append(table.getTableName() + " ");
		String whereCondition = QueryUtil.getConditions(conditions, table, values);
		if(whereCondition != null){
			sb.append(" WHERE " + whereCondition);
		}
		 
		qinfo.setClazz(this.table.getClazz());
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumn(column);
		
		return qinfo;
	}
	
	private Column [] queryColumns;
	 
	
	private QueryInfo doQuery(Column [] columns, Page page) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
        boolean star = false;
		if(columns.length > 0){
			for(Column column : columns){
				String sn = QueryUtil.getColumnName(column, table);
                if("*".equals(sn)){
                    sn = "a_t_.*";
                    star = true;
                }
				if(sb.toString().equals("")){
					sb.append("SELECT " + (this.isDistinct?" distinct ":"") + sn);
				}else{
					sb.append(", " + sn );
				}
			}
		}
		
		if(page != null && ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE && orders.isEmpty()){
			sb.append(", ROWNUM rn_");
		}
		
		setFrom(sb, values, star);
		
		if(page != null){
			if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
				if(orders.isEmpty()){
					if(conditions.isEmpty()){
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " WHERE ROWNUM <= ? ) WHERE rn_ > ? ");
					}else{
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " AND ROWNUM <= ? ) WHERE rn_ > ? ");
					}
				}else{
					sb = new StringBuffer("SELECT * FROM (SELECT a_t_.*, ROWNUM rn_ FROM (" + sb.toString() + ") a_t_ WHERE ROWNUM <= ?) WHERE rn_ > ?  ");
				}
				values.add(new Pair<Object>(null, new Integer(page.getSize() + page.getOffset())));
				values.add(new Pair<Object>(null, new Integer(page.getOffset())));
			}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
				sb.append(" limit ? offset ? ");
				values.add(new Pair<Object>(null, new Integer(page.getSize())));
				values.add(new Pair<Object>(null, new Integer(page.getOffset())));
			}
			
		}
		qinfo.setClazz(this.table.getClazz());
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumns(Arrays.asList(columns));
		qinfo.setPage(page);
		
		return qinfo;
	}
	
	private QueryInfo doQueryDistinct(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
		String sn = QueryUtil.getColumnName(column, table);
		sb.append("SELECT " + sn);
		 
		setFrom(sb, values, false);
		 
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumn(column);
		
		return qinfo;
	}
	
	
	private void setFrom(StringBuffer sb, List<Pair<Object>> values, boolean star) throws QueryException{
		
		sb.append(" FROM ");
		sb.append(table.getTableName() + " " + (star?"a_t_ ":""));
		String whereCondition = QueryUtil.getConditions(conditions, table, values);
		if(whereCondition != null){
			sb.append(" WHERE " + whereCondition);
		}
		
		String groupString = QueryUtil.getGroups(groups, table);
				
		if(groupString != null){
			sb.append(" GROUP BY ");
			sb.append(groupString);
		}
	 
		String orderString = QueryUtil.getOrders(orders, table);
		if(orderString != null){
			sb.append(" ORDER BY ");
			sb.append(orderString);
		}
		
		String havingString = QueryUtil.getConditions(havings, table, values);
		if(havingString != null){
			sb.append(" HAVING ");
			sb.append(havingString);
		}
		
	}
	
	public IQueryDao<T> createQuery(Column... columns) throws QueryException {
		this.queryColumns = columns;
		this.isDistinct = false;
		this.info = doQuery(columns, null);
		return this;
	}

	@Override
	public IQuery<T> createCount() throws QueryException {
		this.isDistinct = false;
		this.info = doQueryCount(null);
		return this;
	}

	@Override
	public IQuery<T> createCount(Column column) throws QueryException {
		this.isDistinct = false;
		column.setFunction("count");
		this.info = doQueryCount(column);
		return this;
	}

	@Override
	public IQuery<T> createPage(Page page) throws QueryException {
		this.isDistinct = false;
		this.info = doQuery(this.queryColumns, page);
		return this;
	}


	@Override
	public QueryInfo getQueryInfo() {
		return this.info;
	}

	@Override
	public IQuery<T> createQuery(Class<?> clazz, Column... columns)
			throws QueryException {
		this.isDistinct = false;
		this.queryColumns = columns;
		this.info = doQuery(columns, null);
		this.info.setClazz(clazz);
		return this;
	}

	@Override
	public IQuery<T> createDistinctString(Column column) throws QueryException {
		this.isDistinct = false;
		this.info = doQueryDistinct(column);
		return this;
	}

	@Override
	public IQuery<T> createDistinctQuery(Class<?> clazz, Column... columns)
			throws QueryException {
		this.isDistinct = true;
		this.queryColumns = columns;
		this.info = doQuery(columns, null);
		this.info.setClazz(clazz);
		return this;
	}

	
	
	 
 
 
}
