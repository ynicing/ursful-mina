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
import com.ursful.framework.database.annotaion.RdLargeString;
import com.ursful.framework.database.annotaion.RdTable;
import com.ursful.framework.database.data.DataType;
import com.ursful.framework.database.page.Pair;

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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SQLHelper delete(Object obj) throws TableException{
		
		Class clazz = obj.getClass();
		
		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException("error bean");
		}
		String tableName = table.name();
		
		StringBuffer sql = new StringBuffer("DELETE FROM ");
		
		sql.append(tableName + " ");
		
		List<Pair<Object>> parameters = new ArrayList<Pair<Object>>();
		List<String> ps = new ArrayList<String>();
		Pair primaryKey = null;
		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			if(column != null){
				try {
					
					field.setAccessible(true);
					Object fo = field.get(obj);
					if(fo != null){
						if(column.unique()){
							primaryKey = new Pair(column.name(), field.getType().getSimpleName(), fo);
						}else{
							ps.add(column.name() + " = ? ");
							parameters.add(new Pair("", field.getType().getSimpleName(), fo));
						}
					}else{
						if(!column.allowNull()){
							throw new TableException("不能为空");
						}
					}
				} catch (IllegalArgumentException e) {
					throw new TableException("illegal arg.");
				} catch (IllegalAccessException e) {
					throw new TableException("illegal access");
				}
			}
		}
		if(primaryKey != null){
			sql.append(" WHERE ");
			sql.append(primaryKey.getName() + " = ? ");
			parameters.clear();
			parameters.add(new Pair("", primaryKey.getType(), primaryKey.getValue()));
		}else{
			if(!ps.isEmpty()){
				sql.append(" WHERE ");
				sql.append(ps.toString().substring(1, ps.toString().length()-1));
			}
		}
		
		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		return helper;
				
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SQLHelper update(Object obj, boolean updateNull) throws TableException{
		
		Class clazz = obj.getClass();
		
		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException("error bean");
		}
		String tableName = table.name();
		
		StringBuffer sql = new StringBuffer("UPDATE ");
		
		sql.append(tableName);
		sql.append(" SET ");
		
		List<Pair<Object>> parameters = new ArrayList<Pair<Object>>();
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
						if(column.unique()){
							primaryKey = new Pair(column.name(), type, fo);
						}else{
							ps.add(column.name() + " = ? ");
							RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
							if(ls != null){
								if(!"".equals(ls.name())){
									parameters.add(new Pair(ls.name(), type, fo));
								}else{
									if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
										parameters.add(new Pair("text",type,  fo));
									}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
										parameters.add(new Pair("clob", type, fo));
									}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.SQLServer){
										parameters.add(new Pair("ntext",type,  fo));
									}else{
										parameters.add(new Pair("",type,  fo));
									}
								}
							}else{
								parameters.add(new Pair("", type, fo));
							}
						}
					}else{
						if(!column.allowNull()){
							throw new TableException("不能为空");
						}
					}
				} catch (IllegalArgumentException e) {
					throw new TableException("illegal arg.");
				} catch (IllegalAccessException e) {
					throw new TableException("illegal access");
				}
			}
		}
		sql.append(ps.toString().substring(1, ps.toString().length()-1));
		sql.append(" WHERE ");
		sql.append(primaryKey.getName() + " = ? ");
		parameters.add(new Pair("", primaryKey.getType(), primaryKey.getValue()));
		
		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		
		//LogUtil.info("SQL:" + sql, SQLHelperCreator.class);
		
		return helper;
				
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SQLHelper save(Object obj) throws TableException{
		
		Class clazz = obj.getClass();
		
		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException("error bean");
		}
		String tableName = table.name();
		
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		
		sql.append(tableName);
		sql.append("(");
		
		List<Pair<Object>> parameters = new ArrayList<Pair<Object>>();
		List<String> ps = new ArrayList<String>();
		List<String> vs = new ArrayList<String>();
		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			if(column != null){
				try {
					
					field.setAccessible(true);
                    String type = field.getType().getSimpleName();
					Object fo = field.get(obj);
					if(fo != null){
						ps.add(column.name());
						//parameters.add(fo);
						vs.add("?");
						RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
						if(ls != null){
							if(!"".equals(ls.name())){
								parameters.add(new Pair(ls.name(), type, fo));
							}else{
								if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
									parameters.add(new Pair("text", type, fo));
								}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
									parameters.add(new Pair("clob", type, fo));
								}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.SQLServer){
									parameters.add(new Pair("ntext", type, fo));
								}else{
									parameters.add(new Pair("", type, fo));
								}
							}
						}else{
							parameters.add(new Pair("", type, fo));
						}
					}else{
						if(!column.allowNull()){
							throw new TableException("不能为空");
						}
					}
				} catch (IllegalArgumentException e) {
					throw new TableException("illegal arg.");
				} catch (IllegalAccessException e) {
					throw new TableException("illegal access");
				}
			}
		}
		sql.append(ps.toString().substring(1, ps.toString().length()-1));
		sql.append(")");
		sql.append(" VALUES(");
		sql.append(vs.toString().substring(1, vs.toString().length()-1));
		sql.append(")");
		
		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		return helper;
				
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SQLHelper get(Object obj) throws TableException{
		
		Class clazz = obj.getClass();
		
		RdTable table = (RdTable)clazz.getAnnotation(RdTable.class);
		if(table == null){
			throw new TableException("error bean");
		}
		String tableName = table.name();
		
		StringBuffer sql = new StringBuffer("SELECT * ");
		sql.append("FROM ");
		sql.append(tableName);
		sql.append(" WHERE 1 = 1 ");
		
		List<Pair<Object>> parameters = new ArrayList<Pair<Object>>();
		Map<String, DataType> types = new HashMap<String, DataType>();
		for(Field field : clazz.getDeclaredFields()){
			RdColumn column = (RdColumn) field.getAnnotation(RdColumn.class);
			if(column != null){
				try {
					DataType dt =  DataType.getDataType(field.getType().getSimpleName());
					types.put(field.getName(), dt);
					
					field.setAccessible(true);
					Object fo = field.get(obj);
					
					if(fo != null){
						sql.append(" AND " + column.name() + " = ? ");
						parameters.add(new Pair("",field.getType().getSimpleName(), fo));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
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
		
		List<Pair<Object>> parameters = new ArrayList<Pair<Object>>();
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
						RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
						if(ls != null){
							if(!"".equals(ls.name())){
								parameters.add(new Pair(ls.name(),type, fo));
							}else{
								if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.MYSQL){
									parameters.add(new Pair("text",type, fo));
								}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.ORACLE){
									parameters.add(new Pair("clob",type, fo));
								}else if(ConnectionManager.getManager().getDatabaseType() == DatabaseType.SQLServer){
									parameters.add(new Pair("ntext",type, fo));
								}else{
									parameters.add(new Pair("",type, fo));
								}
							}
						}else{
							parameters.add(new Pair("",type, fo));
						}
						
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		SQLHelper helper = new SQLHelper();
		helper.setSql(sql.toString());
		helper.setParameters(parameters);
		return helper;
				
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static  <T> List<T> listEntity(Class<?> clazz, ResultSet rs) {
		List<T> list = new ArrayList<T>();
		try {
			while (rs.next()) {
				T t = (T)clazz.newInstance();
				newClass(clazz, rs, t);
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return list;
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

	@SuppressWarnings("rawtypes")
	public static <T> void newClass(Class clazz, ResultSet rs, T t)
			throws IllegalAccessException, SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 1; i <= meta.getColumnCount(); i++){
			map.put(meta.getColumnLabel(i), meta.getColumnName(i));
		}
		List<Field> fields = getFieldList(clazz);
		for(Field field : fields){
			if(map.containsKey(field.getName())){
				Object obj = getFieldObject(rs, field, field.getName());
				field.setAccessible(true);
				field.set(t, obj);;
				continue;
			}
			
			RdColumn column = (RdColumn)field.getAnnotation(RdColumn.class);
			if(column == null){
				continue;
			}
			Object obj = getFieldObject(rs, field, column.name());
			field.setAccessible(true);
			field.set(t, obj);;
		}
	}

	public static <T> Object getFieldObject(ResultSet rs, Field field,
			String columnName) throws IllegalAccessException, SQLException {
		Object obj = null;
		
		ResultSetMetaData meta = rs.getMetaData();
		boolean hasName = false;
		for(int i = 1; i <= meta.getColumnCount(); i++){
			if(meta.getColumnLabel(i).toLowerCase().equals(columnName.toLowerCase())){
				hasName = true;
				break;
			}
		}
		if(!hasName){
			return null;
		}
		
		switch (DataType.getDataType(field.getType().getSimpleName())) {
		case INTEGER:
			obj = rs.getInt(columnName);
			break;
		case STRING:
			RdLargeString ls = (RdLargeString) field.getAnnotation(RdLargeString.class);
			if(ls != null && "clob".equals(ls.name())){
				StringBuffer sb = new StringBuffer();
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
			}else{
				obj = rs.getString(columnName);
			}
			break;
		case DOUBLE:
			obj = rs.getDouble(columnName);
			break;
		case FLOAT :
			obj = rs.getFloat(columnName);
			break;
		case LONG:
			obj = rs.getLong(columnName);
			break;
		case DATE:
            Timestamp ts =  rs.getTimestamp(columnName);
            if(ts != null) {
                obj = new Date(ts.getTime());
            }
			break;	
		default:
			break;
		}
		return obj;
	}
	
	
	public static void setParameter(PreparedStatement ps, List<Pair<Object>> objects) throws SQLException{
		for(int i = 0; i < objects.size(); i++){
			Pair<Object> pair = objects.get(i);
			Object obj = pair.getValue();
			String largeString = pair.getName();
			DataType type =  DataType.getDataType(pair.getType());

			switch (type) {
			case INTEGER:
				if(obj == null){
                    ps.setInt(i + 1, 0);
                }else{
                    ps.setInt(i + 1, (Integer) obj);
                }
				break;
			case STRING:
				if((obj != null) && "clob".equals(largeString)){
					Clob clob = new SerialClob(obj.toString().toCharArray());
					ps.setClob(i + 1, clob);
				}else{
					ps.setString(i + 1, (String)obj);
				}
				break;
			case DOUBLE:
                if(obj == null){
                    ps.setDouble(i + 1, 0);
                }else{
                    ps.setDouble(i + 1, (Double)obj);
                }

				break;
			case FLOAT :
                if(obj == null){
                    ps.setFloat(i + 1, 0);
                }else{
                    ps.setFloat(i + 1, (Float) obj);
                }

				break;
			case LONG:
                if(obj == null){
                    ps.setLong(i + 1, 0);
                }else{
                    ps.setLong(i + 1, (Long) obj);
                }

                break;
			case DATE:
                if(obj == null){
                    ps.setTimestamp(1, null);
                }else {
                    ps.setTimestamp(i + 1, new Timestamp(((Date) obj).getTime()));
                }
			default:
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
                case INTEGER:
                    ps.setInt(i + 1, (Integer) obj);
                    break;
                case STRING:
                    ps.setString(i + 1, (String)obj);
                    break;
                case DOUBLE:
                    ps.setDouble(i + 1, (Double)obj);
                    break;
                case FLOAT :
                    ps.setFloat(i + 1, (Float)obj);
                    break;
                case LONG:
                    ps.setLong(i + 1, (Long)obj);
                    break;
                case DATE:
                    ps.setTimestamp(i + 1, new Timestamp(((Date)obj).getTime()));
                default:
                    break;
            }
        }
    }
	
	
	
    /*其它类型请在此处添加*/
    private static final String TYPE_INTEGER = "Integer";
    private static final String TYPE_STRING = "String";
    private static final String TYPE_DATE = "Date";//java.util.Date
    private static final String TYPE_DOUBLE = "Double";
    private static final String TYPE_FLOAT = "Float";

    private static final String TYPE_SQL = "sql";
    private static final String TYPE_OBJECTS = "objects";

    private static final String TYPE_GET = "get";
    private static final String TYPE_SET = "set";

    /**
     * 从SqlAndObjects对象中获取对象列表
     *
     * @param map
     * @return
     */
    public static Object[] getObjects(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        return (Object[]) map.get(TYPE_OBJECTS);
    }

    /**
     * 从SqlAndObjects对象中获取sql语句
     *
     * @param map
     * @return
     */
    public static String getSql(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        return map.get(TYPE_SQL).toString();
    }

    /**
     * 获取删除sql语句
     *
     * @param table
     * @param id
     * @return
     */
    public static String deleteSql(String table, String id) {
        String dbname = getDatabaseName(id);
        return "DELETE FROM " + table + " WHERE " + dbname + " = ? ";
    }

    /**
     * 查询列表
     *
     * @param table      表
     * @param conditions 查询条件
     * @param orders      排序
     * @return
     */
    public static Map<String, Object> queryList(String table, Map<String, Object> conditions, Pair<?> ... orders) {
        return query(table, conditions, orders, false);
    }

    /**
     * 查询数量
     *
    
    public static Map<String, Object> queryCount(String table, Map<String, Object> conditions) {
        return query(table, conditions, null, true);
    }
	*/
    
    public static Map<String, Object> getObject(String table, String key, String value) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put(key, value);
        Pair<?> p = null;
        return queryList(table, temp, p);
    }

    /**
     * 获取查询语句
     *
     * @param table
     * @param conditions
     * @param isCount
     * @return
     */
    private static Map<String, Object> query(String table, Map<String, Object> conditions, Pair<?>[] orders, boolean isCount) {

        Map<String, Object> temp = new HashMap<String, Object>();

        List<Object> objects = new ArrayList<Object>();

        List<String> cons = new ArrayList<String>();

        if (conditions != null && !conditions.isEmpty()) {
            Set<String> keys = conditions.keySet();
            for (String key : keys) {
                String dbname = getDatabaseName(key);
                String value = conditions.get(key).toString();
                if(value.endsWith("%") || value.startsWith("%")) {
                    cons.add(" AND " + dbname.toString() + " like ? ");
                }else{
                    if("".equals(value)) {
                        cons.add(" AND ("+dbname+" IS NULL OR "+dbname+" = ?) ");
                    } else {
                        cons.add(" AND " + dbname.toString() + " = ? ");
                    }

                }
                objects.add(value);

            }
        }

        String sql = null;

        StringBuilder sortOrder = new StringBuilder();
        if (orders != null && orders.length > 0) {
            sortOrder.append(" ORDER BY");
            int i = 0;
            for (Pair<?> order : orders){
                if(i++ > 0) {
                    sortOrder.append(",");
                }
                sortOrder.append(" ").append(getDatabaseName(order.getName()))
                        .append(" ").append(order.getValue());
            }
        }

        String para = " * ";
        if (isCount) {
            para = " count(*) ";
        }
        if (cons.isEmpty()) {
            sql = "SELECT " + para + " FROM " + table + " " + sortOrder;
        } else {
            String condition = cons.toString().substring(1, cons.toString().length() - 1).replaceAll(","," ");
            sql = "SELECT " + para + " FROM " + table + " WHERE 1 = 1 " + condition + " " + sortOrder;
            ;
        }

        temp.put(TYPE_SQL, sql);
        temp.put(TYPE_OBJECTS, objects.toArray());

        return temp;
    }

    /**
     * 获取更新数据库的sql语句以及对象列表，该对象列表与sql语句中的参数顺序保持一致
     *
     * @param id    更新主键
     * @param obj
     * @param clazz
     * @param table
     * @return
     */
    public static Map<String, Object> updateSqlAndObjects(String id, Object obj, Class<?> clazz, String table) {
        Map<String, Object> temp = new HashMap<String, Object>();
        try {
            List<Field> fields = getField(clazz);
            List<String> columns = new ArrayList<String>();
            String condition = null;
            Object object = null;
            List<Object> objs = new ArrayList<Object>();
            for (Field field : fields) {
                String name = field.getName();//字段名称，如：userName
                String dbname = getDatabaseName(name);
                String methodName = getMethodName(TYPE_GET, name);
                Object value = clazz.getMethod(methodName).invoke(obj);
                if (id.equals(name)) {
                    condition = dbname + " = ? ";
                    object = value;
                } else {
                    if (value != null) {
                        columns.add(dbname + " = ? ");
                        objs.add(value);
                    }
                }
            }

            String columnNames = columns.toString().substring(1, columns.toString().length() - 1);

            String sql = "UPDATE " + table + " SET " + columnNames + " WHERE " + condition;
            temp.put("sql", sql);
            objs.add(object);//条件值放最后
            temp.put("objects", objs.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    /**
     * 获取插入数据库的sql语句以及对象列表，该对象列表与sql语句中的参数顺序保持一致
     *
     * @param obj
     * @param clazz
     * @param table
     * @return
     */
    public static Map<String, Object> insertSqlAndObjects(Object obj, Class<?> clazz, String table) {
        Map<String, Object> temp = null;

        try {
            List<Field> fields = getField(clazz);
            List<String> columns = new ArrayList<String>();
            List<String> counts = new ArrayList<String>();
            List<Object> objs = new ArrayList<Object>();
            for (Field field : fields) {
                String name = field.getName();//字段名称，如：userName
                String dbname = getDatabaseName(name);
                String methodName = getMethodName(TYPE_GET, name);
                columns.add(dbname);
                counts.add("?");

                objs.add(clazz.getMethod(methodName).invoke(obj));
            }

            String columnNames = columns.toString().substring(1, columns.toString().length() - 1);
            String columnCounts = counts.toString().substring(1, counts.toString().length() - 1);

            String sql = "INSERT INTO " + table + "(" + columnNames + ") VALUES(" + columnCounts + ")";
            temp = new HashMap<String, Object>();
            temp.put(TYPE_SQL, sql);
            temp.put(TYPE_OBJECTS, objs.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }


    /**
     * 获取属性列表
     *
     * @param clazz
     * @return
     */
    private static List<Field> getField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        List<Field> fds = new ArrayList<Field>();
        List<Field> results=new ArrayList<Field>();
        fds.addAll(Arrays.asList(fields));
        if (!clazz.getSuperclass().getName().equals(Object.class.getName())) {
            List<Field> fdds = getField(clazz.getSuperclass());
            fds.addAll(fdds);
        }

        for(int i=0;i<fds.size();i++){
            Field field=fds.get(i);
            String index= Modifier.toString(field.getModifiers());
            if(index!=null&&index.indexOf("final")!=-1){
            }else{
                results.add(field);
            }
        }
        return results;
    }

    /**
     * 获取Field
     *
     * @param clazz
     * @return
     */
    public static Field getFieldByName(Class<?> clazz, String fieldName){
        Field field = null;
        try{
            field = clazz.getDeclaredField(fieldName);
        }catch (NoSuchFieldException e){
            if (!clazz.getSuperclass().getName().equals(Object.class.getName())) {
                field = getFieldByName(clazz.getSuperclass(), fieldName);
            }
        }
        return field;
    }

    /**
     * 将ResultSet转换为clazz的对象
     *
     * @param clazz
     * @param rs
     * @return
     */
    public static Object getObjectFromResultSet(Class<?> clazz, ResultSet rs) {

        Object object = null;
        try {
            object = clazz.newInstance();

            ResultSetMetaData metaData = rs.getMetaData();//获取查询字段信息
            for (int i = 1; i <= metaData.getColumnCount(); i++){
                String dbname = metaData.getColumnName(i);//获取数据库字段名称 role_name
                String name = getFieldName(dbname);//获取对应model字段如 roleName
                String methodName = getMethodName(TYPE_SET, name);//获取方法名称：setRoleName

                Field field = getFieldByName(clazz, name); //获取Field，同时获取Filed类型
                if (field == null) {
                    continue;
                }
                Method method = clazz.getMethod(methodName, field.getType());//获取Method
                if (method == null){
                    continue;
                }
                String simpleName =  field.getType().getSimpleName();
                Object para = getValueFromResultSet(rs, simpleName, dbname);//获取值
                if (para == null){
                    continue;
                }
                //对object设置值
                method.invoke(object, new Object[]{para});
            }

            /*
            for (Field field : fields) {
                String name = field.getName(); //字段名称，如：userName
                Class type = field.getType();//参数类型

                String dbname = getDatabaseName(name);
                String methodName = getMethodName(TYPE_SET, name);

                String simpleName = type.getSimpleName();//获取简单类名称，如Integer， String等


                Object para = getValueFromResultSet(rs, simpleName, dbname);

                //获取方法
                Method method = clazz.getMethod(methodName, type);
                //对object设置值
                method.invoke(object, new Object[]{para});
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    private static Object getValueFromResultSet(ResultSet rs, String simpleName, String dbname) throws Exception {
        Object para = null;
        if (rs != null) {//rs各种判断，注意类型转换
            if (simpleName.equals(TYPE_STRING)) {
                para = rs.getString(dbname);
            } else if (simpleName.equals(TYPE_INTEGER)) {
                para = rs.getInt(dbname);
            } else if (simpleName.equals(TYPE_DOUBLE)) {
                para = rs.getDouble(dbname);
            } else if (simpleName.equals(TYPE_FLOAT)) {
                para = rs.getFloat(dbname);
            } else if (simpleName.equals(TYPE_DATE)) {
                java.sql.Date date = rs.getDate(dbname);
                if (date != null) {
                    para = new Date(date.getTime());
                }
            }
        }
        return para;
    }

    /**
     * 获取数据库名称
     *
     * @param name
     * @return
     */
    private static String getDatabaseName(String name) {
        StringBuffer dbname = new StringBuffer();//数据库表字段，如：user_name
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ('A' <= ch && 'Z' >= ch) {//严格按照java命名规范与数据库命名规范，如：userName，首字不得大写
                dbname.append(("_" + ch).toLowerCase());
            } else {
                dbname.append(ch);
            }
        }
        return dbname.toString();
    }

    /**
     * 获取field称, 根据数据库字段名称获取field名称
     *
     * @param name
     * @return
     */
    public static String getFieldName(String name) {
        StringBuffer dbname = new StringBuffer();//数据库表字段，如：user_name
        boolean toUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch == '_'){
                toUpper = true;
                continue;
            }
            if (toUpper){
                dbname.append(("" + ch).toUpperCase());
                toUpper = false;
            }else{
                dbname.append(("" + ch).toLowerCase());
            }
        }
        return dbname.toString();
    }

    /**
     * 获取字段对应的set或get方法
     *
     * @param type set或get
     * @param name
     * @return
     */
    private static String getMethodName(String type, String name) {
        StringBuffer methodName = new StringBuffer(type);//获取get方法: getUserName()
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (i == 0) {//get 方法第一个字母大写
                methodName.append(("" + ch).toUpperCase());
            } else {
                methodName.append(ch);
            }
        }
        return methodName.toString();
    }

}
