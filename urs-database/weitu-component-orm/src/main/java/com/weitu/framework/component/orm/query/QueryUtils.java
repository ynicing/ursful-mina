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


import com.weitu.framework.component.orm.Manager;
import com.weitu.framework.component.orm.helper.SQLHelper;
import com.weitu.framework.component.orm.ISQLScript;
import com.weitu.framework.component.orm.exception.QueryException;
import com.weitu.framework.component.orm.support.*;
import com.weitu.framework.component.orm.annotation.RdColumn;
import com.weitu.framework.component.orm.annotation.RdTable;
import com.weitu.framework.core.util.ListUtils;

import java.lang.reflect.Field;
import java.util.*;

public class QueryUtils {

    private static final int ERROR_QUERY_TABLE = 100;


    public static String displayNameOrAsName(String displayName, String columnName){
        if(displayName.toUpperCase().equals(columnName.toUpperCase())){
            String [] names = columnName.split("_");
            StringBuffer sb = new StringBuffer(names[0].toLowerCase());
            for(int i = 1; i < names.length; i++){
                String n = names[i];
                sb.append(n.substring(0,1).toUpperCase());
                sb.append(n.substring(1).toLowerCase());
            }
            return sb.toString();
        }else{
            return displayName;
        }


    }


    public static String fieldNameToColumn(String name){
		if(name == null || name.length() == 0){
			return null;
		}
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < name.length(); i++){
            char c = name.charAt(i);
            if('A' <= c && c <= 'Z'){
                sb.append("_");
                sb.append((c+"").toLowerCase());
            }else{
                sb.append(c);
            }
        }

		return sb.toString();
	}

//	private static String columnToFieldName(String name){
//		if(name == null || name.length() == 0){
//			return null;
//		}
//		String [] names = name.split("_");
//		StringBuffer sb = new StringBuffer(names[0].toLowerCase());
//		for(int i = 1; i < names.length; i++){
//			String n = names[i];
//			sb.append(n.substring(0,1).toUpperCase());
//			sb.append(n.substring(1));
//		}
//		return sb.toString();
//	}
	
//	private static Field getFieldOrSuper(String name, Class<?> clazz){
//		Class<?> tmp = clazz;
//		Field field = null;
//		while(tmp != null){
//			try {
//				field = tmp.getDeclaredField(columnToFieldName(name));
//			} catch (Exception e) {
//				tmp = tmp.getSuperclass();
//				continue;
//			}
//			break;
//		}
//		return field;
//	}


	
	public static String getOrders(List<Order> orders) throws QueryException{
        List<String> temp = new ArrayList<String>();
        if(orders == null){
            return null;
        }
		for(Order order : orders){
            temp.add(parseColumn(order.getColumn()) + " " + order.getOrder());
		}
        if(temp.size() > 0){
            return ListUtils.join(temp, ",");
        }
		return null;
	}


	public static String getGroups(List<Column> columns) throws QueryException{
		List<String> temp = new ArrayList<String>();
		for(Column column : columns){
			temp.add(parseColumn(column));
		}
		if(temp.size() > 0){
			return ListUtils.join(temp, ",");
		}
		return null;
	}
	

	

	public static String parseColumn(Column column) throws QueryException{

        if(column == null){
            throw new QueryException(ERROR_QUERY_TABLE, "this column is null");
        }
        if(column.getName() == null){
            throw new QueryException(ERROR_QUERY_TABLE, "this column name is null.");
        }

		StringBuffer sb = new StringBuffer();
		if(column.getFunction() != null){
			sb.append(column.getFunction() + "(");
            if(column.getAlias() != null && !"".equals(column.getAlias())){
                sb.append(column.getAlias() + ".");
            }
            sb.append(column.getName());
			sb.append(")");
		}else{
            if(column.getAlias() != null && !"".equals(column.getAlias())){
                sb.append(column.getAlias() + ".");
            }
            sb.append(column.getName());
        }
		if(column.getAsName() != null){
			sb.append(" AS " + column.getAsName());
		}
		
		return sb.toString();
	}


    //同一张表怎么半？ select * from test t1, test t2

    public static String getConditions(List<Condition> cds, List<Pair> values) throws QueryException{
        if(cds == null){
            return null;
        }
        List<String> ands = new ArrayList<String>();

        for(Condition condition : cds){
            List<String> ors = new ArrayList<String>();
            List<Expression> orExpressions = condition.getOrExpressions();
            for(int i = 0; i < orExpressions.size(); i++){
                Expression expression = orExpressions.get(i);
                SQLPair sqlPair = parseExpression(expression);
                if(sqlPair != null){
                    ors.add(sqlPair.getSql());
                    if(sqlPair.getPair() != null) {//column = column
                        for(Pair pair : sqlPair.getPair()) {
                            values.add(pair);
                        }
                    }
                }
            }
            if(ors.size() > 0){
                ands.add("(" + ListUtils.join(ors, " OR ") + ") ");
            }
            List<Expression> andExpressions = condition.getAndExpressions();
            for(int i = 0; i < andExpressions.size(); i++){
                Expression expression = andExpressions.get(i);
                SQLPair sqlPair = parseExpression(expression);
                if(sqlPair != null){
                    ands.add(sqlPair.getSql());
                    if(sqlPair.getPair() != null) {
                        for(Pair pair : sqlPair.getPair()) {
                            values.add(pair);
                        }
                    }
                }
            }

        }
        return ListUtils.join(ands, " AND ");
    }

    public static SQLHelper parseScript(ISQLScript script, Object [] objects, boolean updateNull){
        StringBuffer sql = new StringBuffer();
        if(script.columns().length != objects.length){
            return null;
        }
        switch (script.type()){
            case TABLE_SAVE:
                sql.append("INSERT INTO " + script.table());
                break;
            case TABLE_UPDATE:
                sql.append("UPDATE " + script.table());
                break;
            case TABLE_DELETE:
                sql.append("DELETE FROM " + script.table());
        }

        return null;
    }

    public static String getConditions(Express [] expresses, List<Pair> values) throws QueryException{
        if(expresses == null){
            return null;
        }
        List<String> ands = new ArrayList<String>();
            for(int i = 0; i < expresses.length; i++){
                Expression expression = expresses[i].getExpression();
                SQLPair sqlPair = parseExpression(expression);
                if(sqlPair != null){
                    ands.add(sqlPair.getSql());
                    if(sqlPair.getPair() != null) {
                        for(Pair pair : sqlPair.getPair()) {
                            values.add(pair);
                        }
                    }
                }
            }

        return ListUtils.join(ands, " AND ");
    }

    public static SQLPair parseExpression(Expression expression){
        SQLPair sqlPair = null;
        if(expression == null || expression.getLeft() == null || expression.getValue() == null){
            return sqlPair;
        }
        String conditionName = parseColumn(expression.getLeft());
        Object conditionValue = expression.getValue();

        if(conditionValue instanceof Column){
            sqlPair = new SQLPair(conditionName + " = " + parseColumn((Column) conditionValue));
            return sqlPair;
        }
        switch (expression.getType()) {
            case CDT_Equal:
                sqlPair = new SQLPair(" " + conditionName + " = ?", new Pair(conditionValue));
                break;
            case CDT_NotEqual:
                sqlPair = new SQLPair(" " + conditionName + " != ?", new Pair(conditionValue));
                break;
            case CDT_More:
                sqlPair = new SQLPair(" "+ conditionName + " > ?", new Pair(conditionValue));
                break;
            case CDT_MoreEqual:
                sqlPair = new SQLPair(" "+ conditionName + " >= ?", new Pair(conditionValue));
                break;
            case CDT_Less:
                sqlPair = new SQLPair(" "+ conditionName + " < ?", new Pair(conditionValue));
                break;
            case CDT_LessEqual:
                sqlPair = new SQLPair(" "+ conditionName + " <= ?", new Pair(conditionValue));
                break;
            case CDT_Like:
                sqlPair = new SQLPair(" "+ conditionName + " like ?", new Pair("%" +conditionValue + "%"));
                break;
            case CDT_LikeLeft:
                sqlPair = new SQLPair(" "+ conditionName + " like ?", new Pair("%" +conditionValue));
                break;
            case CDT_LikeRight:
                sqlPair = new SQLPair(" "+ conditionName + " like ?", new Pair(conditionValue + "%"));
                break;
            case CDT_In:
                //List/ Collection/set
                //if(Collection)
                if(Collection.class.isAssignableFrom(conditionValue.getClass())){
                    List<String> temp = new ArrayList<String>();
                    List<Pair> pairs = new ArrayList<Pair>();
                    Collection collection = (Collection) conditionValue;
                    Iterator iterator = collection.iterator();
                    while (iterator.hasNext()){
                        temp.add("?");
                        pairs.add(new Pair(iterator.next()));
                    }
                    sqlPair = new SQLPair(" " + conditionName + " in (" + ListUtils.join(temp, ",") + ")", pairs);
                }else{
                    throw new RuntimeException("使用in必须时实现Collection接口的类。");
                }
                //sqlPair = new SQLPair(" "+ conditionName + " like ?", new Pair(conditionValue + "%"));
                break;
            default:
                break;
        }
        return sqlPair;
    }

    public static TableInfo getTableInfoFromClass(Class clazz) throws QueryException{
        TableInfo info = new TableInfo();
        info.setName(clazz.getSimpleName());
        RdTable rt = (RdTable)clazz.getAnnotation(RdTable.class);
        if(rt == null){
            throw new QueryException(ERROR_QUERY_TABLE, "this bean has not map table");
        }
        info.setTableName(rt.name());
        info.setClazz(clazz);
        Map<String, ColumnInfo> columns = new HashMap<String, ColumnInfo>();
        for(Field f : clazz.getDeclaredFields()){
            RdColumn rc = (RdColumn)f.getAnnotation(RdColumn.class);
            if(rc != null){
                ColumnInfo in = new ColumnInfo();
                in.setColumnName(rc.name());
                in.setName(f.getName());
                in.setField(f);
                in.setDataType(DataType.getDataType(f.getType().getSimpleName()));
                columns.put(in.getColumnName(), in);
            }
        }
        if(columns.isEmpty()){
            throw new QueryException(ERROR_QUERY_TABLE, "this bean has not map columns");
        }
        info.setColumns(columns);
        return info;
    }


    public static QueryInfo doQuery(QueryHelper helper, Page page) throws QueryException {


		QueryInfo qinfo = new QueryInfo();

		List<Pair> values = new ArrayList<Pair>();

		StringBuffer sb = new StringBuffer();
        List<String> temp = new ArrayList<String>();
        sb.append("SELECT ");
        List<Column> returnColumns = helper.getReturnColumns();
		if(returnColumns != null && returnColumns.size() > 0){
            if(helper.isDistinct()){
                sb.append(" DISTINCT ");
            }
			for(Column column : returnColumns){
                temp.add(QueryUtils.parseColumn(column));
			}
            sb.append(ListUtils.join(temp, ","));
		}else {
            sb.append(" * ");
        }

		if(page != null && Manager.getManager().getDatabaseType() == DatabaseType.ORACLE){
			sb.append(", ROWNUM rn_");
		}

        sb.append(getWordAfterFrom(helper, values, false));


		if(page != null){
			if(Manager.getManager().getDatabaseType() == DatabaseType.ORACLE){
				if(helper.getOrders().isEmpty()){
					if(helper.getConditions().isEmpty()){
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " ROWNUM <= ? ) WHERE rn_ > ? ");
					}else{
						sb = new StringBuffer("SELECT * FROM (" + sb.toString() + " AND ROWNUM <= ? ) WHERE rn_ > ? ");
					}
				}else{
					sb = new StringBuffer("SELECT * FROM (SELECT a_t_.*, ROWNUM rn_ FROM (" + sb.toString() + ") WHERE a_t_ ROWNUM <= ?) WHERE rn_ > ?  ");
				}
				values.add(new Pair(new Integer(page.getSize() + page.getOffset())));
				values.add(new Pair(new Integer(page.getOffset())));
			}else if(Manager.getManager().getDatabaseType() == DatabaseType.MYSQL){
				sb.append(" LIMIT ? OFFSET ? ");
				values.add(new Pair(new Integer(page.getSize())));
				values.add(new Pair(new Integer(page.getOffset())));
			}

		}
		qinfo.setClazz(helper.getReturnClass());
		qinfo.setSql(sb.toString());
		qinfo.setValues(values);
		qinfo.setColumns(helper.getReturnColumns());

		return qinfo;
	}

    public static QueryInfo doQueryCount(QueryHelper helper) throws QueryException {


        QueryInfo qinfo = new QueryInfo();

        List<Pair> values = new ArrayList<Pair>();

        StringBuffer sb = new StringBuffer();

        sb.append("SELECT ");
        if(helper.isDistinct()){
            sb.append("COUNT( DISTINCT ");
            List<Column> returnColumns = helper.getReturnColumns();
            List<String> temp = new ArrayList<String>();
            for(Column column : returnColumns){
                temp.add(QueryUtils.parseColumn(column));
            }
            sb.append(ListUtils.join(temp, ","));
            sb.append(")");
        }else{
            sb.append("COUNT(*) ");
        }
//        List<Column> returnColumns = helper.getReturnColumns();
//        if(returnColumns != null && returnColumns.size() > 0){
//            if(helper.isDistinct()){
//                sb.append("DISTINCT ");
//            }
//            for(Column column : returnColumns){
//                temp.add(QueryUtils.parseColumn(column));
//            }
//            sb.append(ListUtils.join(temp, ","));
//        }else {
//            sb.append(" * ");
//        }
//        sb.append(") ");

        sb.append(getWordAfterFrom(helper, values, true));

        qinfo.setClazz(Integer.class);
        qinfo.setSql(sb.toString());
        qinfo.setValues(values);

        return qinfo;
    }

    public static String getWordAfterFrom(QueryHelper helper, List<Pair> values, boolean count) throws QueryException{
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ");

        if(helper.getTable() != null){
            RdTable table = (RdTable)helper.getTable().getAnnotation(RdTable.class);
            sb.append(table.name());
        }else{
            List<String> words = new ArrayList<String>();
            Map<String, Class<?>> aliasMap = helper.getAliasTable();
            List<String> aliasList = helper.getAliasList();
            for(String alias : aliasList) {
                RdTable table = (RdTable)aliasMap.get(alias).getAnnotation(RdTable.class);
                words.add(table.name() + " AS " + alias);
            }
            sb.append(ListUtils.join(words, ","));
        }

        String join = join(helper.getJoins(), values);
        if(join != null && !"".equals(join)){
            sb.append(join);
        }

        String whereCondition = QueryUtils.getConditions(helper.getConditions(), values);
        if(whereCondition != null && !"".equals(whereCondition)){
            sb.append(" WHERE " + whereCondition);
        }

        String groupString = QueryUtils.getGroups(helper.getGroups());

        if(groupString != null && !"".equals(groupString)){
            sb.append(" GROUP BY ");
            sb.append(groupString);
        }

        String havingString = QueryUtils.getConditions(helper.getHavings(), values);
        if(havingString != null && !"".equals(havingString)){
            sb.append(" HAVING ");
            sb.append(havingString);
        }

        if(!count) {
            String orderString = QueryUtils.getOrders(helper.getOrders());
            if (orderString != null && !"".equals(orderString)) {
                sb.append(" ORDER BY ");
                sb.append(orderString);
            }
        }

        return sb.toString();

    }

    public static String join(List<Join> joins, List<Pair> values) throws QueryException{
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
}
