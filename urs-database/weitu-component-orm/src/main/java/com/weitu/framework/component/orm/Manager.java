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
package com.weitu.framework.component.orm;

import com.weitu.framework.component.orm.support.DatabaseType;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {

	private DatabaseType type = DatabaseType.MYSQL;
	
	public DatabaseType getDatabaseType(){
		return type;
	}

    private DataSource dataSource;

	public static void main(String[] args) {




	}
	

    //private static ThreadLocal<PoolConnection> threadLocal = new ThreadLocal<PoolConnection>();

    private static Manager manager = new Manager();

    private static final int LEFT = 3;
    private static final int TIMES = 10;
    private static AtomicInteger atomic = new AtomicInteger(0);


    public static Manager getManager(){
    	return manager;
    }

    private Manager(){
    	
    }
  
    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    	try {

            Connection conn = null;
            conn = dataSource.getConnection();

            if(type == null){
                String dbName = conn.getMetaData().getDatabaseProductName().toLowerCase();
                //LogUtil.info("database type : " + dbName, ConnectionManager.class);
                if(dbName.indexOf("mysql") > -1){
                    type = DatabaseType.MYSQL;
                }else if(dbName.indexOf("oracle") > -1){
                    type = DatabaseType.ORACLE;
                }
            }
            close(null, null, conn, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }



    public synchronized Connection getConnection(DataSource ds){
        if(ds == null){
            if(dataSource != null){
                return DataSourceUtils.getConnection(dataSource);
            }
        }else{
            return DataSourceUtils.getConnection(ds);
        }
        return null;

    }
    

    public synchronized void close(ResultSet rs, Statement stmt, Connection conn, DataSource ds){
    	try {
    		if(rs != null){
    			rs.close();
        	}
    		if(stmt != null){
    			stmt.close();
        	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if(ds != null) {
                DataSourceUtils.releaseConnection(conn, ds);
            }else{
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }
    }




}