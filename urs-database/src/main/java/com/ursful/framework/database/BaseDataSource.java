package com.ursful.framework.database;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * Created by ynice on 6/3/17.
 */
public class BaseDataSource implements DataSource{

    private DatabaseInfo info;

    public BaseDataSource(DatabaseInfo info){
        this.info = info;
        try {
            Driver driver = (Driver)Class.forName(info.getDriver()).newInstance();
            DriverManager.registerDriver(driver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Connection getConnection() throws SQLException{
        Connection conn = null;
        if (info != null) {
            try {
                Class.forName(info.getDriver());
                conn = DriverManager.getConnection(info.getUrl(),
                        info.getUsername(), info.getPassword());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
