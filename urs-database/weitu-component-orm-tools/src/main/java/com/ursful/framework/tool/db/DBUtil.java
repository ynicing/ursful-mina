package com.ursful.framework.tool.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ynice on 3/27/17.
 */
public class DBUtil {

    static Map<String, String> types = new HashMap<String, String>();
    static{
        types.put("java.math.BigDecimal", "java.lang.Double");
        types.put("oracle.sql.TIMESTAMP", "java.util.Date");
        types.put("[B", "blob-java.lang.String");
        types.put("java.sql.Timestamp", "java.util.Date");
        types.put("java.sql.Date", "java.util.Date");
    }

    public static List<String> getTables(Information info, String db){
        String sql = "";
        DatabaseType type = DatabaseType.getDatabaseType(info.getType());
        switch(type){
            case MySQL:
                sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"+db+"'";
                break;
            case Oracle:
                sql = "select table_name from user_tables";
                break;
            case SQLServer:
                break;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String> temp = new ArrayList<String>();
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);
            while(rs.next()){
                temp.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return temp;
    }

    public static List<String> getDBS(Information info){
        String sql = "";
        DatabaseType type = DatabaseType.getDatabaseType(info.getType());
        switch(type){
            case MySQL:
                sql = "select SCHEMA_NAME from information_schema.schemata";
                break;
            case Oracle:
                sql = "select tablespace_name from dba_tablespaces";
                sql = "select DISTINCT default_tablespace from user_users";
                break;
            case SQLServer:
                break;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String> temp = new ArrayList<String>();
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);
            while(rs.next()){
                temp.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return temp;
    }

    public static Connection getConnection(Information info){
        Connection conn = null;
        try {
            Class.forName(info.getDriver());
            conn = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static boolean isNull(Information info, String tableName, String columnName, String dbName){
        String sql = "";
        boolean result = false;
        DatabaseType type = DatabaseType.getDatabaseType(info.getType());
        switch(type){
            case MySQL:
                //String scheme = getMySQLScheme();
                sql = "select is_nullable from information_schema.columns where table_schema='" + dbName
                        + "' and table_name='"+tableName+"' and column_name='"+ columnName +"'";
                break;
            case Oracle:
                sql = "select au.constraint_type from user_cons_columns cu, user_constraints au where " +
                        " cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " +
                        " and cu.table_name='"+tableName+"' and cu.column_name='"+columnName+"'";

                sql = "select nullable from user_tab_cols where table_name='" + tableName +
                        "' and column_name='"+ columnName + "'";
                break;
            case SQLServer:
                break;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                String ctype = rs.getString(1);
                if(ctype.toLowerCase().equals("yes")){
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return result;
    }

    public static boolean isPrimaryKey(Information info, String tableName, String columnName, String dbName){
        String sql = "";
        boolean result = false;
        DatabaseType type = DatabaseType.getDatabaseType(info.getType());
        switch(type){
            case MySQL:
                //String scheme = getMySQLScheme();
                sql = "select column_key from information_schema.columns where table_schema='" + dbName
                        + "' and table_name='"+tableName+"' and column_name='"+ columnName +"'";
                break;
            case Oracle:
                sql = "select au.constraint_type from user_cons_columns cu, user_constraints au where " +
                        " cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " +
                        " and cu.table_name='"+tableName+"' and cu.column_name='"+columnName+"'";

                /*
                sql = "select nullable from user_tab_cols where table_name='" + tableName +
                        "' and column_name='"+ columnName + "'";*/
                break;
            case SQLServer:
                break;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                String ctype = rs.getString(1);
                if(ctype.toLowerCase().equals("p") || (ctype.toLowerCase().equals("pri"))
                        ||ctype.toLowerCase().equals("y")){
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return result;
    }


    public static String getTableComment(Information info, String tableName, String dbName){


        DatabaseType dt = DatabaseType.getDatabaseType(info.getType());

        String sql = "select comments from USER_TAB_COMMENTS WHERE TABLE_NAME='" + tableName + "'";
        if(dt == DatabaseType.MySQL){
            sql = "SELECT table_comment FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"+dbName+"' AND TABLE_NAME='" + tableName + "'";
        }
        String result = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                result = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return result;
    }

    public static Map<String, String> getTableColumnComment(Information info, String tableName, String dbName){

        DatabaseType dt = DatabaseType.getDatabaseType(info.getType());

        String sql = "select column_name, comments from USER_COL_COMMENTS WHERE TABLE_NAME='" + tableName + "'";
        if(dt == DatabaseType.MySQL){
            sql = "select column_name, column_comment from information_schema.columns where table_schema='" + dbName
                    + "' and table_name='"+tableName+"'";
        }
        Map<String, String> result = new HashMap<String, String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                /*Column c = new Column(rs.getString(1),
                        rs.getString(2),
                        "YES".equalsIgnoreCase(rs.getString(3)));*/
                System.out.println(rs.getString(1) + "=====>" + rs.getString(2));
                result.put(rs.getString(1).toUpperCase(), rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return result;
    }

    public static Map<String, Boolean> getTableColumnNull(Information info, String tableName, String dbName){

        DatabaseType dt = DatabaseType.getDatabaseType(info.getType());

        String sql = "select column_name, nullable from USER_TAB_COLUMNS WHERE TABLE_NAME='" + tableName + "'";
        if(dt == DatabaseType.MySQL){
            sql = "select column_name, is_nullable from information_schema.columns where table_schema='" + dbName
                    + "' and table_name='"+tableName+"'";
        }
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                /*Column c = new Column(rs.getString(1),
                        rs.getString(2),
                        "YES".equalsIgnoreCase(rs.getString(3)));*/
                boolean b = "YES".equalsIgnoreCase(rs.getString(2)) || "Y".equalsIgnoreCase(rs.getString(2));
                result.put(rs.getString(1).toUpperCase(), b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return result;
    }

    public static Map<String, String> getTableColumns(Information info, String tableName){
        String sql = "select * from " + tableName;
        Map<String, String> map = new HashMap<String, String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(info);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData data = rs.getMetaData();
            int count = data.getColumnCount();

            for(int i = 1; i <= count; i++){
                String className = data.getColumnClassName(i);
                String columnName = data.getColumnName(i);



                if(types.containsKey(className)){
                    map.put(columnName, types.get(className));
                }else{
                    map.put(columnName, className);
                }

                System.out.println(columnName + "=====>" + className + "=====>" + map.get(columnName));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (Exception e2) {
            }

        }
        return map;
    }

    /*
    public static DatabaseType getDatabaseType(){
        Connection conn = getConnection();
        String db  = "";
        try {
            db = conn.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(db.indexOf("mysql") > -1){
            return DatabaseType.MySQL;
        }else if(db.indexOf("oracle") > -1){
            return DatabaseType.Oracle;
        }else{
            return DatabaseType.SQLServer;
        }
    }
*/
}
