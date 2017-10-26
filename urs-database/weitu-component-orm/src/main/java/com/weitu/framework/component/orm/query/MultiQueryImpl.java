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
package com.weitu.framework.component.orm.query;

import com.weitu.framework.component.orm.IMultiQuery;
import com.weitu.framework.component.orm.annotation.RdTable;
import com.weitu.framework.component.orm.exception.QueryException;
import com.weitu.framework.component.orm.support.*;
import com.weitu.framework.core.util.ListUtils;

import java.util.*;

public class MultiQueryImpl implements IMultiQuery {

    private List<String> aliasList = new ArrayList<String>();
	private Map<String, Class<?>> aliasTable = new HashMap<String, Class<?>>();
	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Condition> havings = new ArrayList<Condition>();
	private List<Column> groups = new ArrayList<Column>();
	private List<Order> orders = new ArrayList<Order>();
	private List<Join> joins = new ArrayList<Join>();

    private Class<?> returnClass;
    private List<Column> returnColumns = new ArrayList<Column>();


	private boolean distinct = false;
	
	
//	private QueryInfo doQuery(Class<?> clazz, Column [] columns, Page page) throws QueryException {
//
//		QueryInfo qinfo = new QueryInfo();
//
//		List<Pair> values = new ArrayList<Pair>();
//
//		StringBuffer sb = new StringBuffer();
//        List<String> temp = new ArrayList<String>();
//        sb.append("SELECT ");
//		if(columns.length > 0){
//            if(isDistinct){
//                sb.append(" DISTINCT ");
//            }
//			for(Column column : columns){
//                temp.add(QueryUtils.parseColumn(column));
//			}
//            sb.append(ListUtils.join(temp, ","));
//		}else {
//            sb.append(" * ");
//        }
//
//		if(page != null && ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE && orders.isEmpty()){
//			sb.append(", ROWNUM rn_");
//		}
//
//		setFrom(sb, values);
//
//		if(page != null){
//			if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
//				if(orders.isEmpty()){
//					if(conditions.isEmpty()){
//						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " ROWNUM <= ? ) WHERE rn_ > ? ");
//					}else{
//						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " AND ROWNUM <= ? ) WHERE rn_ > ? ");
//					}
//				}else{
//					sb = new StringBuffer("SELECT * FROM (SELECT a_t_.*, ROWNUM rn_ FROM (" + sb.toString() + ") WHERE a_t_ ROWNUM <= ?) WHERE rn_ > ?  ");
//				}
//				values.add(new Pair(new Integer(page.getSize() + page.getOffset())));
//				values.add(new Pair(new Integer(page.getOffset())));
//			}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
//				sb.append(" LIMIT ? OFFSET ? ");
//				values.add(new Pair(new Integer(page.getSize())));
//				values.add(new Pair(new Integer(page.getOffset())));
//			}
//
//		}
//		qinfo.setClazz(clazz);
//		qinfo.setSql(sb.toString());
//		qinfo.setValues(values);
//		qinfo.setColumns(Arrays.asList(columns));
//		qinfo.setPage(page);
//
//		return qinfo;
//	}
	

    /*
	private QueryInfo doQuery(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair> values = new ArrayList<Pair>();
		
		StringBuffer sb = new StringBuffer();
		String sn = column.getName();
		sb.append("SELECT " + (this.isDistinct?" distinct ":"") + sn);
		 
		setFrom(sb, values);
		qinfo.setClazz(String.class);
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumn(column);
		
		return qinfo;
	}*/
	
	
	/*private QueryInfo doQueryCount(Column column) throws QueryException {
		
		QueryInfo qinfo = new QueryInfo();
		
		List<Pair> values = new ArrayList<Pair>();
		
		StringBuffer sb = new StringBuffer();
		if(column != null){
            sb.append("SELECT " + QueryUtils.parseColumn(column) +") ");
		}else{
            sb.append("SELECT " + Expression.EXPRESSION_COUNT + "("+ Expression.EXPRESSION_ALL +") ");
		}

		setFrom(sb, values);
		 
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
        if(columns != null) {
            qinfo.setColumns(Arrays.asList(columns));
        }
        qinfo.setClazz(Integer.class);
		qinfo.setPage(page);
		
		return qinfo;
	}*/


	private void setFrom(StringBuffer sb, List<Pair> values) throws QueryException{

		sb.append(" FROM ");
        List<String> words = new ArrayList<String>();
        for(String alias : aliasList) {
            words.add(aliasTable.get(alias) + " AS " + alias);
        }
        sb.append(ListUtils.join(words, ","));

        String join = join(joins, values);
        if(join != null && !"".equals(join)){
            sb.append(join);
        }

		String whereCondition = QueryUtils.getConditions(conditions, values);
		if(whereCondition != null){
			sb.append(" WHERE " + whereCondition);
		}

		String groupString = QueryUtils.getGroups(groups);

		if(groupString != null){
			sb.append(" GROUP BY ");
			sb.append(groupString);
		}

		String havingString = QueryUtils.getConditions(havings, values);
		if(havingString != null){
			sb.append(" HAVING ");
			sb.append(havingString);
		}

        String orderString = QueryUtils.getOrders(orders);
        if(orderString != null){
            sb.append(" ORDER BY ");
            sb.append(orderString);
        }

    }

    public String join(List<Join> joins, List<Pair> values) throws QueryException{
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < joins.size(); i++){
            Join join = joins.get(i);
            if(join.getClazz() == null){
                continue;
            }
            RdTable rdTable = (RdTable)join.getClazz().getAnnotation(RdTable.class);
            if(rdTable == null){
                continue;
            }
            String tableName = rdTable.name();
            switch (join.getType()){
                case FULL_JOIN:
                    sb.append(" FULL JOIN ");
                    break;
                case INNER_JOIN:
                    sb.append(" INNER JOIN ");
                    break;
                case LEFT_JOIN:
                    sb.append(" LEFT JOIN ");
                    break;
                case RIGHT_JOIN:
                    sb.append(" RIGHT JOIN ");
                    break;
            }
            String alias = join.getAlias();

            sb.append(tableName + " AS " + alias);

            List<Condition> temp = join.getConditions();

            String cdt = QueryUtils.getConditions(temp, values);
            if(cdt != null && !"".equals(cdt)) {
                sb.append(" ON ");
                sb.append(cdt);
            }
        }

        return sb.toString();
    }


	
//	public IMultiQuery createCount() throws QueryException {
//		this.isDistinct = false;
//		this.qinfo = doQueryCount(null);
//		return this;
//	}

	
//	public IMultiQuery createCount(Column column) throws QueryException {
//		column.setFunction(Expression.EXPRESSION_COUNT);
//		this.isDistinct = false;
//		this.qinfo = doQueryCount(column);
//		return this;
//	}

	
//	public IMultiQuery createDistinctString(Column column) throws QueryException {
//		this.isDistinct = false;
//		this.qinfo = doQuery(column);
//		return this;
//	}

//	private Page page;
	
	
//	public IMultiQuery createPage(Page page) throws QueryException {
//		this.page = page;
//		this.isDistinct = false;
//		this.qinfo = doQuery(this.returnClass, this.columns, this.page);
//		return this;
//	}

	public IMultiQuery createQuery(Class<?> clazz, Column... columns) throws QueryException {
		this.returnClass = clazz;
        if(columns != null) {
            for (Column column : columns) {
                returnColumns.add(column);
            }
        }
		return this;
	}

	
//	public IMultiQuery createDistinctQuery(Class<?> clazz, Column... columns)
//			throws QueryException {
//		this.isDistinct = true;
//		this.returnClass = clazz;
//		this.columns = columns;
//		this.qinfo = doQuery(clazz, columns, null);
//		return this;
//	}



    /////////////////////////////////

    public IMultiQuery table(String alias, Class<?> clazz) throws QueryException {
        if(clazz != null) {
            RdTable rdTable = (RdTable)clazz.getAnnotation(RdTable.class);
            if(rdTable != null) {
                aliasList.add(alias);
                aliasTable.put(alias, clazz);
            }
        }
        return this;
    }

    public IMultiQuery where(Column left, Object value, ExpressionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        conditions.add(new Condition().and(new Expression(left, value, type)));

        return this;
    }

    public IMultiQuery where(Column left, Column value) {
        conditions.add(new Condition().and(new Expression(left, value)));
        return this;
    }

    public IMultiQuery where(Condition condition) {
        conditions.add(condition);
        return this;
    }


    public IMultiQuery group(Column ... columns) {
        groups.addAll(Arrays.asList(columns));
        return this;
    }

    public IMultiQuery having(Column left, Object value, ExpressionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        havings.add(new Condition().or(new Expression(left, value, type)));
        return this;
    }

    public IMultiQuery having(Column left, Column value) {
        havings.add(new Condition().and(new Expression(left, value)));
        return this;
    }

    public IMultiQuery having(Condition condition) {
        havings.add(condition);
        return this;
    }



    public IMultiQuery orderDesc(Column column) {
        orders.add(new Order(column, Order.DESC));
        return this;
    }

    public IMultiQuery orderAsc(Column column) {
        orders.add(new Order(column, Order.ASC));
        return this;
    }

    public IMultiQuery join(Join join) throws QueryException {
        joins.add(join);
        return this;
    }

    @Override
    public IMultiQuery distinct() {
        this.distinct = true;
        return this;
    }

    private QueryHelper helper = new QueryHelper();

    @Override
    public QueryHelper queryHelper() {
        helper.setAliasList(aliasList);
        helper.setAliasTable(aliasTable);
        helper.setConditions(conditions);
        helper.setDistinct(distinct);
        helper.setHavings(havings);
        helper.setJoins(joins);
        helper.setOrders(orders);
        helper.setGroups(groups);
        helper.setReturnClass(returnClass);
        helper.setReturnColumns(returnColumns);
        helper.setDistinct(distinct);
        return helper;
    }

    ////////////////////////

//    public QueryInfo getQueryInfo() {
//        return this.qinfo;
//    }


}
