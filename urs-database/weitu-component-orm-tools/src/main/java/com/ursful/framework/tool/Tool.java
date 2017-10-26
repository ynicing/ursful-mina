package com.ursful.framework.tool;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import com.ursful.framework.tool.util.DataType;
import com.ursful.framework.tool.db.DBUtil;
import com.ursful.framework.tool.db.Information;
import com.ursful.framework.tool.util.TextUtil;
import org.apache.commons.lang.*;


/**
create table T_SINGLE(
	sid int primary key,
	name varchar(1000)
) ENGINE=innodb DEFAULT CHARSET=utf8;

 1、给表加注释 
COMMENT ON TABLE TABLENAME IS '用户表'; 

2、查看表的COMMENT 
SELECT * FROM USER_TAB_COMMENTS WHERE TABLE_NAME='TABLENAME'; 
SELECT table_name, table_type, comments FROM USER_TAB_COMMENTS WHERE TABLE_NAME='T_SYS_USER'; 4

3、给字段加注释 
COMMENT ON COLUMN TABLENAME.COLNAME IS 'OOXX'; 

4、查看字段的COMMENT

SELECT table_name, table_type, comments FROM USER_TAB_COMMENTS WHERE TABLE_NAME='T_SYS_USER';
 SELECT table_name, column_name, comments FROM USER_COL_COMMENTS WHERE TABLE_NAME='T_SYS_USER';  
SELECT * FROM USER_COL_COMMENTS WHERE TABLE_NAME='TABLENAME'; 


 *
 */
public class Tool {


	public static void main(String[] args) throws Exception{

        Information info = loadInformation();

        Connection conn = DBUtil.getConnection(info);

        PreparedStatement ps = conn.prepareStatement("select id as sid, sum(order_num) as on1  from t_sys_menu group by sid");

        ResultSet rs = ps.executeQuery();

        ResultSetMetaData  metaData = rs.getMetaData();


        int count = metaData.getColumnCount();

        //java.math.BigDecimal


        while(rs.next()){
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i = 1; i <= count; i++){
                map.put(metaData.getColumnLabel(i), rs.getObject(i));
            }
            System.out.println(map);

        }
        Object obj = null;
        //ArrayUtils.toObject(obj);



        /*
        String dbName =  "urs";
		String folder = "D:/work/eclipse/workspace/uws/src";
		folder = System.getProperty("user.dir") + File.separator + "urs-admin/src/main/java";
        String packageName = "com.ursful.framework.rename.solo";
        String folderMenu = "admin";
        String htmlFolder = "/Users/ynice/Documents/urs/urs-site/src/main/webapp/" + folderMenu;
        String jsFolder = "/Users/ynice/Documents/urs/urs-site/src/main/webapp/adminjs";
        String languageFolder = "/Users/ynice/Documents/urs/urs-site/src/main/webapp/js/bundle";

        List<String> tables = DBUtil.getTables(info, dbName);
        TextUtil.create(info, dbName, tables,packageName, folderMenu, folder, htmlFolder, jsFolder, languageFolder);
        */
    }




//	private static String getMySQLScheme(){
//		int last = info.getUrl().lastIndexOf("3306/") + 5;
//		String scheme = info.getUrl().substring(last);
//		return scheme;
//	}



    public static Information loadInformation(){
        Properties props = new Properties();
        try {
            props.load(Tool.class.getClassLoader().getResourceAsStream("deploy.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String driver = props.getProperty("database.driver");
        String url = props.getProperty("database.url");
        String username = props.getProperty("database.username");
        String password = props.getProperty("database.password");

        Information info = new Information("mysql");

        info.setDriver(driver);
        info.setUrl(url);
        info.setUsername(username);
        info.setPassword(password);

        return info;
    }

}
