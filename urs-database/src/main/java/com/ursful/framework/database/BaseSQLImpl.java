package com.ursful.framework.database;


import java.sql.*;
import java.util.*;
import java.util.Date;

public class BaseSQLImpl  implements IBaseSQL{

    /*
    public void saveModule(Module module){
        String sql = "INSERT INTO T_SYS_MODULE(ID, AUTHOR, NAME, VERSION, URL, LOAD_DATE, DESCRIPTION) VALUES(?, ?, ?, ?, ?, ?, ?)";
        execute(sql, UUID.randomUUID().toString(), module.getAuthor(), module.getName(), module.getVersion(),
                module.getUrl(), new Date(), module.getDescription());
    }*/

    public List<String> getCurrentTables(){

        List<String> temp = new ArrayList<String>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            DatabaseType dt = ConnectionManager.getManager().getDatabaseType();

            String sql = null;
            switch(dt){
                case MYSQL:
                    sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '"+ConnectionManager.getManager().getDatabaseName()+"'";
                    break;
                case ORACLE:
                    sql = "select table_name from user_tables";
                    break;
                case SQLServer:
                    break;
            }

            Connection conn = ConnectionManager.getManager().getConnection();
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
            ConnectionManager.getManager().close(ps, rs);
        }
        return temp;
    }

    public void executeBatch(String sql, Object[][] parameters){
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            for(Object[] objects : parameters) {
                SQLHelperCreator.setParameter(ps, objects);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, rs);
        }
    }

    public int execute(String sql){
        if(sql == null || "".equalsIgnoreCase(sql)){
            return -1;
        }
        int res = -1;
        PreparedStatement ps = null;
        try {
            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, null);
        }
        return res;
    }

    public int execute(String sql, Object ... parameters){
        if(sql == null || "".equalsIgnoreCase(sql)){
            return -1;
        }
        int res = -1;
        PreparedStatement ps = null;
        try {
            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            SQLHelperCreator.setParameter(ps, parameters);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, null);
        }
        return res;
    }

    public List<Object> query(String sql, Object ... parameters){

        List<Object> temp = new ArrayList<Object>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            SQLHelperCreator.setParameter(ps, parameters);
            rs = ps.executeQuery();

            while(rs.next()){
                temp.add(rs.getObject(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, rs);
        }
        return temp;
    }

    public Object queryObject(String sql, Object ... parameters){
        Object temp = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            //LogUtil.info("SQL:" + sql + " Parameters: " + parameters, BaseSQLImpl.class);

            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            SQLHelperCreator.setParameter(ps, parameters);
            rs = ps.executeQuery();

            if(rs.next()){
                temp = rs.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, rs);
        }
        return temp;
    }

    public String queryLatestVersion(String moduleName){
        String temp = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            String sql = "select version from t_sys_module where name = ? order by load_date desc";
            Object [] parameters = new Object[]{moduleName};

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            Connection conn = ConnectionManager.getManager().getConnection();
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
            ConnectionManager.getManager().close(ps, rs);
        }
        return temp;
    }


    public List<Map<String, Object>> queryMap(String sql, Object ... parameters){

        List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            //LogUtil.info("SQL:" + sql + " Parameers: " + parameters, BaseSQLImpl.class);

            Connection conn = ConnectionManager.getManager().getConnection();
            ps = conn.prepareStatement(sql);
            SQLHelperCreator.setParameter(ps, parameters);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            while(rs.next()){
                Map<String, Object> map = new HashMap<String, Object>();
                for(int i = 1; i <= metaData.getColumnCount(); i++){
                    map.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                temp.add(map);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            ConnectionManager.getManager().close(ps, rs);
        }
        return temp;
    }


    //
}
