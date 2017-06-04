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

import com.ursful.framework.database.ColumnInfo;
import com.ursful.framework.database.TableInfo;
import com.ursful.framework.database.annotaion.RdColumn;
import com.ursful.framework.database.annotaion.RdTable;
import com.ursful.framework.database.page.Pair;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryUtil {

    private static final int ERROR_QUERY_TABLE = 100;

	public static Class<?> getFieldClass(Column column, Map<String, TableInfo> infos) throws QueryException{
		TableInfo info = infos.get(column.getAlias());
		return getFieldClass(column, info);
	}
	
	public static Class<?> getFieldClass(Column column, TableInfo info) throws QueryException{
		if(info == null){
			throw new QueryException(ERROR_QUERY_TABLE, "[getField] this alias:" + column.getAlias() + ", has not map any map.");
		}
		Class<?> result = null;
		try {
			if(column.getName().trim().equals("*")){
				result = info.getClazz();
			}else{
				Field f = getFieldOrSuper(column.getName(), info.getClazz());
				if(f == null){
					throw new QueryException(ERROR_QUERY_TABLE, "[getField]" +  column.getName());
				}
				result = f.getType();
			}
		} catch (SecurityException e) {
			throw new QueryException(ERROR_QUERY_TABLE, "[getField]" + e.getMessage());
		}
		return result;
	}
	
	private static Field getFieldOrSuper(String name, Class<?> clazz){
		Class<?> tmp = clazz;
		Field field = null;
		while(tmp != null){
			try {
				field = tmp.getDeclaredField(name);
			} catch (Exception e) {
				tmp = tmp.getSuperclass();
				continue;
			}
			break;
		}
		return field;
	}
	
	public static Class<?> getFieldClass(Column column, Class<?> clazz) throws QueryException{
		Class<?> result = null;
		try {
			if(column.getName().trim().equals("*")){
				result = clazz;
			}else{
				Field f = //clazz.getDeclaredField(column.getName());
						getFieldOrSuper(column.getName(), clazz);
				if(f == null){
					throw new QueryException(ERROR_QUERY_TABLE, "[getField]" +  column.getName());
				}
				result = f.getType();
			}
		} catch (SecurityException e) {
			throw new QueryException(ERROR_QUERY_TABLE, "[getField]" + e.getMessage());
		}
		return result;
	}
	
	public static String getFieldTypeString(Column column, Map<String, TableInfo> infos) throws QueryException{
		TableInfo info = infos.get(column.getAlias());
		return getFieldTypeString(column, info);
	}
	
	public static String getFieldTypeString(Column column, TableInfo info) throws QueryException{
		
		if(info == null){
			throw new QueryException(ERROR_QUERY_TABLE, "[getField] this alias:" + column.getAlias() + ", has not map any map.");
		}
		String sname = null;
		try {
			if(!column.getName().trim().equals("*")){
				Field f = getFieldOrSuper(column.getName(), info.getClazz());
				if(f == null){
					throw new QueryException(ERROR_QUERY_TABLE, "[getField]" +  column.getName());
				}
				sname = f.getType().getSimpleName();
			}
		} catch (SecurityException e) {
			throw new QueryException(ERROR_QUERY_TABLE, "[getField]" + e.getMessage());
		}
		return sname;
	}
	
	public static Field getField(Column column, Map<String, TableInfo> infos) throws QueryException{
		TableInfo info = infos.get(column.getAlias());
		return getField(column, info);
	}
	
	
	public static Field getField(Column column, TableInfo info) throws QueryException{
		if(info == null){
			throw new QueryException(ERROR_QUERY_TABLE, "[getField] this alias:" + column.getAlias() + ", has not map any map.");
		}
		Field sname = null;
		try {
			sname = getFieldOrSuper(column.getName(), info.getClazz());
			if(sname == null){
				throw new QueryException(ERROR_QUERY_TABLE, "[getField]" +  column.getName());
			}
		} catch (SecurityException e) {
			throw new QueryException(ERROR_QUERY_TABLE, "[getField]" + e.getMessage());
		}
		return sname;
	}
	
	public static Field getField(Column column, Class<?> info) throws QueryException{
		Field sname = null;
		try {
			if(column.getAsName() != null){
				sname = getFieldOrSuper(column.getAsName(), info);
			}else{
				sname = getFieldOrSuper(column.getName(), info);
			}
			if(sname == null){
				throw new QueryException(ERROR_QUERY_TABLE, "[getField]" +  column.getName());
			}
		} catch (SecurityException e) {
			throw new QueryException(ERROR_QUERY_TABLE, "[getField]" + e.getMessage());
		}
		return sname;
	}
	
	public static String getOrders(List<Order> orders, TableInfo info) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(Order order : orders){
			sb.append(", " + getColumnName(order.getColumn(), info) + " " + order.getOrder());
		}
		if(sb.length() > 1){
			return sb.substring(1);
		}
		return null;
	}
	
	public static String getOrders(List<Order> orders, Map<String, TableInfo> infos) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(Order order : orders){
			sb.append(", " + getColumnName(order.getColumn(), infos) + " " + order.getOrder());
		}
		if(sb.length() > 1){
			return sb.substring(1);
		}
		return null;
	}
	
	public static String getGroups(List<Column> columns, TableInfo info) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(Column column : columns){
			sb.append(", " + getColumnName(column, info));
		}
		if(sb.length() > 1){
			return sb.substring(1);
		}
		return null;
	}
	
	public static String getGroups(List<Column> columns, Map<String, TableInfo> infos) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(Column column : columns){
			sb.append(", " + getColumnName(column, infos));
		}
		if(sb.length() > 1){
			return sb.substring(1);
		}
		return null;
	}
	
	public static String getConditions(List<Condition> conditions, TableInfo info, List<Pair<Object>> values) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < conditions.size(); i++){
			Condition c = conditions.get(i);
			String conditionName = getColumnName(c.getLeft(), info);
			Object conditionValue = c.getValue();
			if(c.getType() == null){
				sb.append("AND "+ conditionName + " = " + getColumnName((Column) conditionValue, info) + " ");
				continue;
			}
			switch (c.getType()) {
			case CDT_Equal:
				sb.append("AND "+ conditionName + " = ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_NotEqual:
				sb.append("AND "+ conditionName + " != ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_More:
				sb.append("AND "+ conditionName + " > ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_MoreEqual:
				sb.append("AND "+ conditionName + " >= ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_Less:
				sb.append("AND "+ conditionName + " < ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_LessEqual:
				sb.append("AND "+ conditionName + " <= ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_Like:
				sb.append("AND "+ conditionName + " like ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			default:
				break;
			}
		}
	 
		if(sb.length() >= 3){
			return sb.toString().substring(3);
		}
		
		return null;
	}
	
	
	public static String getConditions(List<Condition> conditions, Map<String, TableInfo> infos, List<Pair<Object>> values) throws QueryException{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < conditions.size(); i++){
			Condition c = conditions.get(i);
			String conditionName = getColumnName(c.getLeft(), infos);
			Object conditionValue = c.getValue();
			if(c.getType() == null){
				sb.append("AND "+ conditionName + " = " + getColumnName((Column) conditionValue, infos) + " ");
				continue;
			}
			switch (c.getType()) {
			case CDT_Equal:
				sb.append("AND "+ conditionName + " = ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_NotEqual:
				sb.append("AND "+ conditionName + " != ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_More:
				sb.append("AND "+ conditionName + " > ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_MoreEqual:
				sb.append("AND "+ conditionName + " >= ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_Less:
				sb.append("AND "+ conditionName + " < ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_LessEqual:
				sb.append("AND "+ conditionName + " <= ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			case CDT_Like:
				sb.append("AND "+ conditionName + " like ? ");
				values.add(new Pair<Object>(null, conditionValue));
				break;
			default:
				break;
			}
		}
	 
		if(sb.length() >= 3){
			return sb.toString().substring(3);
		}
		
		return null;
	}
	
	//u.createDate => create_date
	//count(u.createDate) => count(u.create_date)
	public static String getColumnName(Column column, TableInfo info) throws QueryException{
		
		
		String bf = "*";
		 
		if(!column.getName().trim().equals("*")){
			if(info == null){
				throw new QueryException(ERROR_QUERY_TABLE, "this :" + column + ", has not map table.");
			}
			ColumnInfo cinfo = info.getColumns().get(column.getName().trim());
			if(cinfo == null){
				throw new QueryException(ERROR_QUERY_TABLE, "this :" + column.getName() + ", has not map any columm.");
			}
			bf = cinfo.getColumnName();
		}
		
		StringBuffer sb = new StringBuffer();
		if(column.getFunction() != null){
			sb.append(column.getFunction() + "(");
		}
		if(column.getAlias() != null){
			sb.append(column.getAlias() + ".");
		}
		sb.append(bf);
		if(column.getFunction() != null){
			sb.append(")");
		}
		if(column.getAsName() != null){
			sb.append(" " + column.getAsName());
		}
		
		return sb.toString();
	}
	
	public static String getColumnName(Column column, Map<String, TableInfo> infos) throws QueryException{
		TableInfo info = infos.get(column.getAlias());
		return getColumnName(column, info);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				columns.put(in.getName(), in);
			}
		}
		if(columns.isEmpty()){
			throw new QueryException(ERROR_QUERY_TABLE, "this bean has not map columns");
		}
		info.setColumns(columns);
		return info;
	}
}
