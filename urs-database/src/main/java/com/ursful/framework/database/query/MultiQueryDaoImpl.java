package com.ursful.framework.database.query;


import com.ursful.framework.database.*;
import com.ursful.framework.database.page.Page;
import com.ursful.framework.database.page.Pair;

import java.util.*;


public class MultiQueryDaoImpl<T> implements IMultiQueryDao<T>{
	
	private Map<String, TableInfo> tables = new HashMap<String, TableInfo>();

	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Condition> ons = new ArrayList<Condition>();
	private List<Condition> havings = new ArrayList<Condition>();
	private List<Column> groups = new ArrayList<Column>();
	private List<Order> orders = new ArrayList<Order>();
	
	private String leftTable;
	private String rightTable;
	
	private boolean isDistinct = false;
	
	
	private QueryInfo doQuery(Class<?> clazz, Column [] columns, Page page) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
		if(columns.length > 0){
			for(Column column : columns){
				String sn = QueryUtil.getColumnName(column, tables);
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
		
		setFrom(sb, values);
		
		if(page != null){
			if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
				if(orders.isEmpty()){
					if(conditions.isEmpty()){
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " ROWNUM <= ? ) WHERE rn_ > ? ");
					}else{
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " AND ROWNUM <= ? ) WHERE rn_ > ? ");
					}
				}else{
					sb = new StringBuffer("SELECT * FROM (SELECT a_t_.*, ROWNUM rn_ FROM (" + sb.toString() + ") WHERE a_t_ ROWNUM <= ?) WHERE rn_ > ?  ");
				}
				values.add(new Pair<Object>(null, new Integer(page.getSize() + page.getOffset())));
				values.add(new Pair<Object>(null, new Integer(page.getOffset())));
			}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
				sb.append(" limit ? offset ? ");
				values.add(new Pair<Object>(null, new Integer(page.getSize())));
				values.add(new Pair<Object>(null, new Integer(page.getOffset())));
			}
			
		}
		qinfo.setClazz(clazz);
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumns(Arrays.asList(columns));
		qinfo.setPage(page);
		
		return qinfo;
	}
	
	
	private QueryInfo doQuery(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
		String sn = QueryUtil.getColumnName(column, tables);
		sb.append("SELECT " + (this.isDistinct?" distinct ":"") + sn);
		 
		setFrom(sb, values);
		qinfo.setClazz(String.class);
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumn(column);
		
		return qinfo;
	}
	
	
	private QueryInfo doQueryCount(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair<Object>> values = new ArrayList<Pair<Object>>();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		if(column != null){
			String sn = QueryUtil.getColumnName(column, tables);
			sb.append(sn);
		}else{
			sb.append(" count(*) ");
		}
		 
		setFrom(sb, values);
		 
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumns(Arrays.asList(columns));
		qinfo.setPage(page);
		
		return qinfo;
	}
	
	private String getTables(){
		StringBuffer sb = new StringBuffer();
		for(String alias : tables.keySet()){
			if(alias.equals(leftTable) || alias.equals(rightTable)){
				continue;
			}
			sb.append(", " + tables.get(alias).getTableName() + " " + alias);
		}
		if(sb.length() > 1){
			return sb.substring(1);
		}
		return null;
	}

	private void setFrom(StringBuffer sb, List<Pair<Object>> values) throws QueryException{
		
		sb.append(" FROM ");
		
		sb.append(getTables());
		
		if(tables.containsKey(leftTable)){
			sb.append(" left join " + tables.get(leftTable).getTableName() + " " + leftTable);
		}
		
		if(tables.containsKey(rightTable)){
			sb.append(" right join " + tables.get(rightTable).getTableName() + " " + rightTable);
		}
		
		String onString = QueryUtil.getConditions(ons, tables, values);
		if(onString != null){
			sb.append(" ON " + onString);
		}
		
		String whereCondition = QueryUtil.getConditions(conditions, tables, values);
		if(whereCondition != null){
			sb.append(" WHERE " + whereCondition);
		}
		
		String groupString = QueryUtil.getGroups(groups, tables);
				
		if(groupString != null){
			sb.append(" GROUP BY ");
			sb.append(groupString);
		}
	 
		String orderString = QueryUtil.getOrders(orders, tables);
		if(orderString != null){
			sb.append(" ORDER BY ");
			sb.append(orderString);
		}
		
		String havingString = QueryUtil.getConditions(havings, tables, values);
		if(havingString != null){
			sb.append(" HAVING ");
			sb.append(havingString);
		}
		
	}

	@Override
	public IQuery<T> createCount() throws QueryException {
		this.isDistinct = false;
		this.qinfo = doQueryCount(null);
		return this;
	}

	@Override
	public IQuery<T> createCount(Column column) throws QueryException {
		column.setFunction("count");
		this.isDistinct = false;
		this.qinfo = doQueryCount(column);
		return this;
	}

	@Override
	public IQuery<T> createDistinctString(Column column) throws QueryException {
		this.isDistinct = false;
		this.qinfo = doQuery(column);
		return this;
	}

	private Page page;
	
	@Override
	public IQuery<T> createPage(Page page) throws QueryException {
		this.page = page;
		this.isDistinct = false;
		this.qinfo = doQuery(this.returnClass, this.columns, this.page);
		return this;
	}

	@Override
	public QueryInfo getQueryInfo() {
		return this.qinfo;
	}

	private QueryInfo qinfo;
	
	private Class<?> returnClass;
	private Column[] columns;
	
	@Override
	public IMultiQueryDao<T> createQuery(Class<?> clazz, Column... columns) throws QueryException {
		this.returnClass = clazz;
		this.columns = columns;
		this.isDistinct = false;
		this.qinfo = doQuery(clazz, columns, null);
		return this;
	}

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(QueryDaoImpl.class.getClassLoader().getResourceAsStream("deploy.properties"));
		
		DatabaseInfo dbinfo = new DatabaseInfo();
		dbinfo.setDriver(props.getProperty("database.driver"));
		dbinfo.setPassword(props.getProperty("database.password"));
		dbinfo.setUsername(props.getProperty("database.username"));
		dbinfo.setUrl(props.getProperty("database.url"));
		dbinfo.setMaxActive(Integer.parseInt(props.getProperty("database.maxActive")));
		dbinfo.setMinActive(Integer.parseInt(props.getProperty("database.maxIdle")));
		ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);
		
		/*
		IQuery<SysUser> query = new MultiQueryDaoImpl<SysUser>()
				.createAliasTable("u", SysUser.class)
				.createAliasTable("g", SysGroup.class)
				.createAliasTable("ug", SysUserGroup.class)
				.where(new Column("u", SysUser.T_ID), new Column("ug", SysUserGroup.T_USER_ID))
				.where(new Column("g", SysGroup.T_ID), new Column("ug", SysUserGroup.T_GROUP_ID))
				.createQuery(SysUser.class, 
						new Column("u", SysUser.T_NAME), 
						new Column("g", SysGroup.T_NAME, SysUser.T_GROUP_NAME));
		query.createPage(new Page(1, 100));
		
		
		
		IBaseDao<SysUser> baseDao = new BaseDaoImpl<SysUser>();
		Page page = baseDao.queryPage(query, null);
		query.createCount();
		
		query = new MultiQueryDaoImpl<SysUser>()
				.createAliasTable("u", SysUser.class)
				.createRightAliasTable("ug", SysUserGroup.class)
				.on(new Column("u", SysUser.T_ID), new Column("ug", SysUserGroup.T_USER_ID))
				.where(new Column("ug", SysUserGroup.T_GROUP_ID), 2, ConditionType.CDT_Equal)
				.createQuery(SysUser.class, 
						new Column("u", SysUser.T_NAME), 
						new Column("ug", SysUserGroup.T_GROUP_ID, SysUser.T_GROUP_ID));
		query.createPage(new Page(2, 1));
		List<SysUser> users = baseDao.query(query);
		for(SysUser user : users){
		   (user.getName() + "---" + user.getGroupId());
		}
		query.createCount(new Column("u", SysUser.T_NAME));
		  ("count:" + baseDao.queryCount(query));
		
		
		query = new MultiQueryDaoImpl<SysUser>()
				.createAliasTable("u", SysUser.class)
				//.createRightAliasTable("ug", SysUserGroup.class)
				//.on(new Column("u", SysUser.T_ID), new Column("ug", SysUserGroup.T_USER_ID))
				//.where(new Column("ug", SysUserGroup.T_GROUP_ID), 2, ConditionType.CDT_Equal)
				.createDistinctString(new Column("distinct","u", SysUser.T_NAME, null));
		List<String> strings = baseDao.queryDistinctString(query);
		*/
	}
	
	
	@Override
	public IMultiQueryDao<T> createAliasTable(String alias, Class<?> clazz)
			throws QueryException {
		TableInfo table = QueryUtil.getTableInfoFromClass(clazz);
		tables.put(alias, table);
		return this;
	}

	@Override
	public IMultiQueryDao<T> createLeftAliasTable(String alias, Class<?> clazz)
			throws QueryException {
		TableInfo table = QueryUtil.getTableInfoFromClass(clazz);
		tables.put(alias, table);
		leftTable = alias;
		rightTable = null;
		return this;
	}

	@Override
	public IMultiQueryDao<T> createRightAliasTable(String alias, Class<?> clazz)
			throws QueryException {
		TableInfo table = QueryUtil.getTableInfoFromClass(clazz);
		tables.put(alias, table);
		leftTable = null;
		rightTable = alias;
		return this;
	}

	@Override
	public IMultiQueryDao<T> group(Column column) {
		groups.add(column);
		return this;
	}

	@Override
	public IMultiQueryDao<T> having(Column left, Object value,
			ConditionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        if(type == ConditionType.CDT_Like){
            value = value + "%";
        }
		havings.add(new Condition(left, value, type));
		return this;
	}

	@Override
	public IMultiQueryDao<T> on(Column left, Column right) {
		ons.add(new Condition(left, right, null));
		return this;
	}

	@Override
	public IMultiQueryDao<T> where(Column left, Object value, ConditionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        if(type == ConditionType.CDT_Like){
            value = value + "%";
        }
        conditions.add(new Condition(left, value, type));

		return this;
	}

	@Override
	public IMultiQueryDao<T> where(Column left, Column value) {
        conditions.add(new Condition(left, value, null));
		return this;
	}

	@Override
	public IMultiQueryDao<T> orderDesc(Column column) {
		orders.add(new Order(column, "DESC"));
		return this;
	}

	@Override
	public IMultiQueryDao<T> orderAsc(Column column) {
		orders.add(new Order(column, "ASC"));
		return this;
	}


	@Override
	public IQuery<T> createDistinctQuery(Class<?> clazz, Column... columns)
			throws QueryException {
		this.isDistinct = true;
		this.returnClass = clazz;
		this.columns = columns;
		this.qinfo = doQuery(clazz, columns, null);
		return this;
	}
	 

	 
 
}
