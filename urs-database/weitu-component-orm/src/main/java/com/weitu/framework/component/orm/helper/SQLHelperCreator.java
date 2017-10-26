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
package com.weitu.framework.component.orm.helper;

import com.weitu.framework.component.orm.Manager;
import com.weitu.framework.component.orm.error.MessageUtils;
import com.weitu.framework.component.orm.query.QueryUtils;
import com.weitu.framework.component.orm.annotation.RdId;
import com.weitu.framework.component.orm.exception.TableException;
import com.weitu.framework.component.orm.support.*;
import com.weitu.framework.component.orm.annotation.RdColumn;
import com.weitu.framework.component.orm.annotation.RdLargeString;
import com.weitu.framework.component.orm.annotation.RdTable;
import com.weitu.framework.core.util.ListUtils;
import org.springframework.validation.DataBinder;

import javax.sql.rowset.serial.SerialClob;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * 数据库表工具类
 * Created by huangyonghua on 2016/2/16.
 */
public class SQLHelperCreator {


    //不允许删除多条记录，只能根据id删除
    /**
     * 按对象的id删除
     * @param obj
     * @return
     * @throws TableException
     */
    public static SQLHelper delete(Object obj) throws TableException{

        Class clazz = obj.getClass();


        List<Pair> parameters = new ArrayList<Pair>();

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException(MessageUtils.TABLE_NOT_FOUND);
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("DELETE FROM ");

        sql.append(tableName + " ");

        List<String> ps = new ArrayList<String>();
        Pair primaryKey = null;
        for(Field field : clazz.getDeclaredFields()){
            RdId rid = (RdId)field.getAnnotation(RdId.class);
            RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
            if(column != null && rid != null){
                try {
                    field.setAccessible(true);
                    Object fo = field.get(obj);
                    if(fo != null){
                        primaryKey = new Pair(field.getName(), column.name(), field.getType().getSimpleName(), fo, null);
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    throw new TableException(MessageUtils.ILLEGAL_ACCESS);
                } catch (IllegalAccessException e) {
                    throw new TableException(MessageUtils.ILLEGAL_ACCESS);
                }
            }
        }
        if(primaryKey != null && primaryKey.getValue() != null){
            sql.append(" WHERE ");
            sql.append(primaryKey.getColumn() + " = ? ");
            parameters.clear();
            parameters.add(primaryKey);
        }else{
            throw new TableException(MessageUtils.TABLE_DELETE_WITHOUT_ID);
        }
        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(parameters);

        return helper;
    }



    /**
     *
     * @param clazz
     * @param idObject id
     * @return
     * @throws TableException
     */
    public static SQLHelper delete(Class clazz, Object idObject) throws TableException{

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException(MessageUtils.TABLE_NOT_FOUND);
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("DELETE FROM ");

        sql.append(tableName + " ");

        List<Pair> parameters = new ArrayList<Pair>();
        List<String> ps = new ArrayList<String>();
        Pair primaryKey = null;


        for(Field field : clazz.getDeclaredFields()){
            RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
            RdId id = (RdId) field.getAnnotation(RdId.class);
            if(column != null && id != null){
                try {
                    primaryKey = new Pair(field.getName(), column.name(), field.getType().getSimpleName(), idObject, null);
                } catch (IllegalArgumentException e) {
                    throw new TableException(MessageUtils.ILLEGAL_ARGUMENT);
                }
                break;
            }
        }
        if(primaryKey != null && primaryKey.getValue() != null){
            sql.append(" WHERE ");
            sql.append(primaryKey.getColumn() + " = ? ");
            parameters.add(primaryKey);
        }else{
            throw new TableException(MessageUtils.TABLE_DELETE_WITHOUT_ID);
        }

        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(parameters);

        return helper;

    }

    /**
     * 更新 只能使用id，否则初学者不填id 全部更新了。
     * @param obj
     * @param updateNull
     * @return
     * @throws TableException
     */
	public static SQLHelper update(Object obj, boolean updateNull) throws TableException{

		Class clazz = obj.getClass();

		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException(MessageUtils.TABLE_NOT_FOUND);
		}
		String tableName = table.name();

		StringBuffer sql = new StringBuffer("UPDATE ");

		sql.append(tableName);
		sql.append(" SET ");

		List<Pair> parameters = new ArrayList<Pair>();
		List<String> ps = new ArrayList<String>();
		Pair primaryKey = null;
		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);


			if(column != null){
				try {
					field.setAccessible(true);
                    String type = field.getType().getSimpleName();
					Object fo = field.get(obj);
					if(fo != null || updateNull){
                        RdId id = (RdId) field.getAnnotation(RdId.class);
                        if(id != null){
                            if(fo == null){
                                throw new TableException(MessageUtils.TABLE_UPDATE_WITHOUT_ID);
                            }
							primaryKey = new Pair(field.getName(), column.name(), type, fo, null);
						}else{
							ps.add(column.name() + " = ? ");
							RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
                            Pair pair = new Pair(field.getName(), column.name(), type, fo, null);
							if(ls != null){
                                if(Manager.getManager().getDatabaseType() == DatabaseType.MYSQL){
                                    pair.setLargeType(LargeType.LARGE_MYSQL_TEXT);
                                }else if(Manager.getManager().getDatabaseType() == DatabaseType.ORACLE){
                                    pair.setLargeType(LargeType.LARGE_ORACLE_CLOB);
                                }else if(Manager.getManager().getDatabaseType() == DatabaseType.SQLServer){
                                    pair.setLargeType(LargeType.LARGE_SQL_SERVER_NTEXT);
                                }else{
                                    //parameters.add(new Pair("",type,  fo));
                                }
							}
                            parameters.add(pair);
						}
					}
				} catch (IllegalArgumentException e) {
					throw new TableException("illegal arg.");
				} catch (IllegalAccessException e) {
					throw new TableException("illegal access");
				}
			}
		}
        if(primaryKey != null && primaryKey.getValue() != null) {
            sql.append(ps.toString().substring(1, ps.toString().length() - 1));
            sql.append(" WHERE ");
            sql.append(primaryKey.getColumn() + " = ? ");
            parameters.add(primaryKey);
        }else{
            throw new TableException(MessageUtils.TABLE_UPDATE_WITHOUT_ID);
        }
		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);

		//LogUtil.info("SQL:" + sql, SQLHelperCreator.class);

		return helper;

	}

	public static SQLHelper save(Object obj) throws TableException{

		Class clazz = obj.getClass();

		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException(MessageUtils.TABLE_NOT_FOUND);
		}
		String tableName = table.name();

		StringBuffer sql = new StringBuffer("INSERT INTO ");

		sql.append(tableName);
		sql.append("(");

		List<Pair> parameters = new ArrayList<Pair>();
		List<String> ps = new ArrayList<String>();
		List<String> vs = new ArrayList<String>();

		SQLHelper helper = new SQLHelper();

		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			RdId id = (RdId)field.getAnnotation(RdId.class);
			if(column != null){
				try {
					field.setAccessible(true);
                    String type = field.getType().getSimpleName();
					Object fo = field.get(obj);
					if(fo != null ||  (id != null && "String".equals(type))){
                        if(fo == null){
                            fo = UUID.randomUUID().toString();
                        }
						ps.add(column.name());
						//parameters.add(fo);
						vs.add("?");
						RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
                        Pair pair = new Pair(field.getName(), column.name(), type, fo, null);
						if(ls != null){
                            if(Manager.getManager().getDatabaseType() == DatabaseType.MYSQL){
                                pair.setLargeType(LargeType.LARGE_MYSQL_TEXT);
                            }else if(Manager.getManager().getDatabaseType() == DatabaseType.ORACLE){
                                pair.setLargeType(LargeType.LARGE_ORACLE_CLOB);
                            }else if(Manager.getManager().getDatabaseType() == DatabaseType.SQLServer){
                                pair.setLargeType(LargeType.LARGE_SQL_SERVER_NTEXT);
                            }else{
                            //	parameters.add(new Pair("", type, fo));
							}
						}
                        parameters.add(pair);
					}else{//值为空的时候，但是无id，需要自取了
                        if(id  != null){
                            helper.setIdField(field);
                        }
                    }
				} catch (IllegalArgumentException e) {
					throw new TableException(MessageUtils.ILLEGAL_ARGUMENT);
				} catch (IllegalAccessException e) {
					throw new TableException(MessageUtils.ILLEGAL_ACCESS);
				}
			}
		}

        if(ps.size() == 0){
            throw new TableException(MessageUtils.TABLE_SAVE_WITH_ANY_VALUE);
        }
		sql.append(ListUtils.join(ps,","));
		sql.append(") VALUES (");
		sql.append(ListUtils.join(vs, ","));
		sql.append(")");


		helper.setSql(sql.toString());
		helper.setParameters(parameters);

		return helper;

	}


    /**
     * 允许获取 id，匹配，唯一等值
     * 有id根据id获取，其他根据列 等值获取
     * @param obj
     * @return
     * @throws TableException
     */
	public static SQLHelper get(Object obj) throws TableException{

		Class clazz = obj.getClass();

		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException(MessageUtils.TABLE_NOT_FOUND);
		}
		String tableName = table.name();

		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(tableName);

		List<Pair> parameters = new ArrayList<Pair>();
		Map<String, DataType> types = new HashMap<String, DataType>();
        List<String> ps = new ArrayList<String>();
        Pair primaryKey = null;

		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
            if(column != null){
                try {
                    field.setAccessible(true);
                    String type = field.getType().getSimpleName();
                    Object fo = field.get(obj);
                    if(fo != null){
                        RdId id = (RdId) field.getAnnotation(RdId.class);
                        if(id != null){
                            primaryKey = new Pair(field.getName(), column.name(), type, fo, null);
                            break;
                        }else{
                            ps.add(column.name() + " = ? ");
                            parameters.add(new Pair(field.getName(), column.name(), type, fo, null));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    throw new TableException("illegal arg.");
                } catch (IllegalAccessException e) {
                    throw new TableException("illegal access");
                }
            }
		}

        if(primaryKey != null && primaryKey.getValue() != null) {
            sql.append(" WHERE ");
            sql.append(primaryKey.getColumn() + " = ? ");
            parameters.clear();
            parameters.add(primaryKey);
        }else{
            if(ps.size() > 0){
                sql.append(" WHERE " + ListUtils.join(ps, " AND "));
            }
        }

		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		return helper;

	}

    public static SQLHelper query(Class<?> clazz, Express [] expresses) throws TableException{

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException(MessageUtils.TABLE_NOT_FOUND);
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName);
        List<Pair> values = new ArrayList<Pair>();
        if(expresses != null) {
            String conditions = QueryUtils.getConditions(expresses, values);
            if (conditions != null && !"".equals(conditions)) {
                sql.append(" WHERE " + conditions);
            }
        }

        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(values);

        return helper;

    }

    public static SQLHelper query(Class<?> clazz, String [] names, Terms terms, MultiOrder multiOrder, Integer start, Integer size) throws TableException{

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException(MessageUtils.TABLE_NOT_FOUND);
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("SELECT ");
        if(names != null && names.length == 2) {
            sql.append(names[0] + ", " + names[1]);
        }else{
            sql.append("*");
        }
        sql.append(" FROM " + tableName);
        List<Pair> values = new ArrayList<Pair>();
        if(terms != null) {
            String conditions = QueryUtils.getConditions(ListUtils.newList(terms.getCondition()), values);
            if (conditions != null && !"".equals(conditions)) {
                sql.append(" WHERE " + conditions);
            }
        }

        if(multiOrder != null) {
            String orders = QueryUtils.getOrders(multiOrder.getOrders());
            if (orders != null && !"".equals(orders)) {
                sql.append(" ORDER BY " + orders);
            }
        }

        if(start != null && size != null){
            sql.append(" LIMIT ? OFFSET ? ");
            values.add(new Pair(size));
            values.add(new Pair(start));
        }

        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(values);

        return helper;

    }


    public static SQLHelper queryCount(Class<?> clazz, String name, Terms terms) throws TableException{

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException(MessageUtils.TABLE_NOT_FOUND);
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM " + tableName);
        List<Pair> values = new ArrayList<Pair>();
        if(terms != null) {
            String conditions = QueryUtils.getConditions(ListUtils.newList(terms.getCondition()), values);
            if (conditions != null && !"".equals(conditions)) {
                sql.append(" WHERE " + conditions);
            }
        }


        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(values);

        return helper;

    }

    /**
     * 只允许ID
     * @param obj
     * @param clazz
     * @return
     * @throws TableException
     */
    public static SQLHelper get(Object obj, Class clazz) throws TableException{

        RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
        if(table == null){
            throw new TableException("error bean");
        }
        String tableName = table.name();

        StringBuffer sql = new StringBuffer("SELECT * ");
        sql.append("FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        List<Pair> parameters = new ArrayList<Pair>();
        Pair primaryKey = null;
        for(Field field : clazz.getDeclaredFields()){
            RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			RdId id = (RdId) field.getAnnotation(RdId.class);
            if(column != null && id != null && obj != null){
                   // DataType dt =  DataType.getDataType(field.getType().getSimpleName());
                    //types.put(field.getName(), dt);
                primaryKey = new Pair(field.getName(),column.name(), field.getType().getSimpleName(), obj, null);
                break;
            }
        }
        if(primaryKey == null){
            throw new TableException(MessageUtils.TABLE_GET_WITHOUT_ID);
        }

        sql.append(primaryKey.getColumn() + " = ? ");
        parameters.add(primaryKey);

        SQLHelper helper = new SQLHelper();
        helper.setSql(sql.toString());
        helper.setParameters(parameters);
        return helper;

    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SQLHelper query(Object obj) throws TableException{

		Class clazz = obj.getClass();

		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException("error bean");
		}
		String tableName = table.name();

		StringBuffer sql = new StringBuffer("SELECT * FROM ");

		sql.append(tableName + " t ");
		sql.append(" WHERE 1 = 1 ");

		List<Pair> parameters = new ArrayList<Pair>();
		Map<String, DataType> types = new HashMap<String, DataType>();
		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			if(column != null){
				try {
					DataType dt =  DataType.getDataType(field.getType().getSimpleName());
					types.put(field.getName(), dt);
					field.setAccessible(true);
                    String type = field.getType().getSimpleName();
					Object fo = field.get(obj);
					if(fo != null){
						sql.append(" AND " + column.name() + " = ? ");
                        Pair pair = new Pair(field.getName(), column.name(), type, fo, null);
                        //查询不需要这个了。RdLargeString
                        parameters.add(pair);
					}
				} catch (IllegalArgumentException e) {
					throw new TableException(MessageUtils.ILLEGAL_ARGUMENT);
				} catch (IllegalAccessException e) {
                    throw new TableException(MessageUtils.ILLEGAL_ACCESS);
				}
			}
		}

		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		return helper;
				
	}
	

	
	private static List<Field> getFieldList(Class<?> clazz){
		List<Field> list = new ArrayList<Field>();
		while(clazz != null){
			for(Field f : clazz.getDeclaredFields()){
				list.add(f);
			}
			clazz = clazz.getSuperclass();
		}
		return list;
	}

	public static <T> T newClass(Class clazz, ResultSet rs)
			throws IllegalAccessException, SQLException {

        T t = null;
        DataType type = DataType.getDataType(clazz.getSimpleName());

        //String, Map, Bean
        switch (type){
            case STRING:
                Object object = rs.getObject(1);
                if(object != null) {
                    t = (T) object.toString();
                }
                break;
//
//            case BOOLEAN:
//                Object tmp = rs.getObject(1);
//                if(tmp == null){
//                    t = (T)Boolean.valueOf(false);
//                }else {
//                    t = (T) Boolean.valueOf(
//                        "true".equalsIgnoreCase(tmp.toString())
//                        || "1".equalsIgnoreCase(tmp.toString())
//                        || "yes".equalsIgnoreCase(tmp.toString())
//                        || "on".equalsIgnoreCase(tmp.toString())
//                    );
//                }
//                break;
            case MAP :
                ResultSetMetaData metaMap = rs.getMetaData();
                Map<String, Object> tempMap = new HashMap<String, Object>();
                for(int i = 1; i <= metaMap.getColumnCount(); i++){
                    Object obj = rs.getObject(i);
                    if(obj != null) {
                        if (obj instanceof Timestamp) {
                            obj = new Date(((Timestamp) obj).getTime());
                        }
                        //oracle clob
                        /*else{
                            obj.getClass().getSimpleName()
                            Clob clob = rs.getClob(columnName);
                            if(clob != null){
                                Reader reader = clob.getCharacterStream();
                                BufferedReader br = new BufferedReader(reader);
                                String s = null;
                                try {
                                    while((s = br.readLine()) != null){
                                        sb.append(s);
                                    }
                                } catch (Exception e) {
                                }
                            }
                            obj = sb.toString();
                        }*/
                        String key = QueryUtils.displayNameOrAsName(metaMap.getColumnLabel(i), metaMap.getColumnName(i));
                        tempMap.put(key, obj);
                    }
                }
                t = (T)tempMap;
                break;
            case UNKNOWN:
                try {
                    t = (T)clazz.newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }

                ResultSetMetaData meta = rs.getMetaData();

                Map<String, Object> temp = new HashMap<String, Object>();
                for(int i = 1; i <= meta.getColumnCount(); i++){
                    String tmp = QueryUtils.displayNameOrAsName(meta.getColumnLabel(i), meta.getColumnName(i));
                    temp.put(tmp, rs.getObject(meta.getColumnLabel(i)));
                }
                List<Field> fields = getFieldList(clazz);
                for(Field field : fields){
                    if(temp.containsKey(field.getName())){
                        Object obj = getFieldObject(field, temp.get(field.getName()));
                        field.setAccessible(true);
                        field.set(t, obj);;
                    }
                }
                break;
            case INTEGER:
                t = (T)new Integer(rs.getInt(1));
        }

        return t;

	}


	public static <T> Object getFieldObject(Field field, Object object) throws IllegalAccessException, SQLException {
		Object obj = null;
        DataType type = DataType.getDataType(field.getType().getSimpleName());
        if(object != null){
            switch (type) {
                case STRING:
                    RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
                    if(ls != null && ls.type() == LargeType.LARGE_ORACLE_CLOB){
                        StringBuffer sb = new StringBuffer();
                        Clob clob = (Clob)object;
                        if(clob != null){
                            Reader reader = clob.getCharacterStream();
                            BufferedReader br = new BufferedReader(reader);
                            String s = null;
                            try {
                                while((s = br.readLine()) != null){
                                    sb.append(s);
                                }
                            } catch (Exception e) {
                            }
                        }
                        obj = sb.toString();
                    }else{
                        obj = object.toString();
                    }
                    break;
                case DATE:
                    Timestamp ts =  (Timestamp)object;
                    if(ts != null) {
                        obj = new Date(ts.getTime());
                    }
                    break;
                default:
                    DataBinder binder = new DataBinder(field, field.getName());
                    obj = binder.convertIfNecessary(object.toString(), field.getType());
            }
        }

		return obj;
	}
	
	
	public static void setParameter(PreparedStatement ps, List<Pair> objects) throws SQLException{

		//ps.setObject(); 是否可以统一使用
		for(int i = 0; i < objects.size(); i++){
			Pair pair = objects.get(i);
			Object obj = pair.getValue();
			LargeType largeType = pair.getLargeType();
			DataType type =  DataType.getDataType(pair.getType());
			switch (type) {
				case STRING:
					if((obj != null) && (largeType == LargeType.LARGE_ORACLE_CLOB)){
						Clob clob = new SerialClob(obj.toString().toCharArray());
						ps.setClob(i + 1, clob);
					}else{
						ps.setString(i + 1, (String) obj);
					}
					break;
				case DATE:
					if(obj == null){
						ps.setTimestamp(1, null);
					}else {
						ps.setTimestamp(i + 1, new Timestamp(((Date) obj).getTime()));
					}
					break;
				default:
					ps.setObject(i + 1, obj);
					break;
			}
		}
	}


    public static void setParameter(PreparedStatement ps, Object [] objects) throws SQLException{

        if(objects == null){
            return;
        }

        for(int i = 0; i < objects.length; i++){
            Object obj = objects[i];
            DataType type =  DataType.getDataType(obj.getClass().getSimpleName());
            switch (type) {
                case DATE:
                    ps.setTimestamp(i + 1, new Timestamp(((Date)obj).getTime()));
					break;
                default:
					ps.setObject(i + 1, obj);
                    break;
            }
        }
    }
	





}
