package com.ursful.framework.database;


import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionManager{
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	

	private DatabaseType type = null;
	
	public DatabaseType getDatabaseType(){
		return type;
	}

    private String databaseName = null;

    public String getDatabaseName(){
        return databaseName;
    }

    private DataSource dataSource;
	
	public static void main(String[] args) {
		DatabaseInfo info = new DatabaseInfo();
		info.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
		info.setDriver("oracle.jdbc.driver.OracleDriver");
		info.setMaxActive(30);
		info.setMinActive(2);
		info.setPassword("user_imc");
		info.setUsername("user_imc");
		
		ConnectionManager.getManager().init(new BaseDataSource(info), info);


		int c = 10;
		int m = 0;
		while(m < 1000){
			for(int i = 0; i < c; i++){
				m++;
				new Thread(new Runnable() {
					@Override
					public void run() {
						ConnectionManager.getManager().getConnection();
						
						int t = new Random().nextInt(10);
						print("sleep:" + t);
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
						
						ConnectionManager.getManager().close(null, null);
						 
					}
				}).start();
			}
			try {
				Thread.sleep(10000 + new Random().nextInt(10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			c = new Random().nextInt(20);
			 
		}
		while(true);
		
	}
	
	
    private DatabaseInfo info;
      
    private List<PoolConnection> connections = new ArrayList<PoolConnection>(); 
    
    private static ThreadLocal<PoolConnection> threadLocal = new ThreadLocal<PoolConnection>();  
  
    private static ConnectionManager manager = new ConnectionManager();
    
    private static final int LEFT = 3;
    private static final int TIMES = 10;
    private static AtomicInteger atomic = new AtomicInteger(0);
    
    public List<PoolConnection> getConnections(){
    	return connections;
    }
    
    public static ConnectionManager getManager(){
    	return manager;
    }
    
    private ConnectionManager(){
    	
    }
  
    public void init(DataSource dataSource, DatabaseInfo info) {
        this.dataSource = dataSource;
    	//com.mysql.jdbc.Driver oracle.jdbc.driver.OracleDriver
    	this.info = info;
    	//LogUtil.info(new JSONObject(info).toString(), ConnectionManager.class);
        try {

            Driver driver = (Driver)Class.forName(info.getDriver()).newInstance();
            DriverManager.registerDriver(driver);
            for (int i = 0; i < info.getMinActive(); i++) {
            	PoolConnection conn;  
                conn = newConnection();  
                if (conn != null) {  
                	if(type == null){
                		String dbName = conn.getConnection().getMetaData().getDatabaseProductName().toLowerCase();
                		//LogUtil.info("database type : " + dbName, ConnectionManager.class);
                		if(dbName.indexOf("mysql") > -1){
                			type = DatabaseType.MYSQL;
                            String url = info.getUrl();
                            databaseName = url.substring(url.lastIndexOf("/") + 1);
                		}else if(dbName.indexOf("oracle") > -1){
                			type = DatabaseType.ORACLE;
                		}
                	}
                	
                	connections.add(conn);  
                }  
            }  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}





    }  
      
    private static void print(String msg){
    	//LogUtil.debug(sdf.format(new Date()) + ":" +  Thread.currentThread().getName() + " ------> " + msg, ConnectionManager.class);
    }
  
    public synchronized Connection getConnection(){
    	return getPoolConnection().getConnection();
	}
    
    public synchronized PoolConnection getPoolConnection() {  
    	print("request Connection");
    	PoolConnection conn = threadLocal.get();
        if(conn != null){
        	print("return current Connection");
        	return conn;
        }
        synchronized (this) {
        	try {  
            	boolean isNew = false;
            	for(int i = 0; i < connections.size(); i++){
            		PoolConnection pc = connections.get(i);
            		if(!pc.isActive()){
            			print("get From exist");
            			pc.setActive(true);
            			conn = pc;
            			break;
            		}
            	}
                if(conn == null && connections.size() < info.getMaxActive()){  	
            		print("create");
            		isNew = true;
            		conn = newConnection();
                }
                while(conn == null){
            		wait(info.getTimeout()*1000);  
            		conn = getFromConnections();
            	}
                if(isNew){
                	connections.add(conn);
                }
                threadLocal.set(conn);
                print("get Connection");
            } catch (SQLException e) {  
                e.printStackTrace();  
            } catch (ClassNotFoundException e) {  
                e.printStackTrace();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
		}
        return conn;  
    }  
    
    private synchronized PoolConnection getFromConnections(){
    	PoolConnection pcc = null;
    	synchronized(this){
			for(PoolConnection pc : connections){
	    		if(!pc.isActive()){
	    			pc.setActive(true);
	    			pcc = pc;
	    			break;
	    		}
	    	}
    	}
    	return pcc;
    }
  
    private synchronized PoolConnection newConnection()  
            throws ClassNotFoundException, SQLException {  
        Connection conn = dataSource.getConnection();
        return new PoolConnection(conn);  
    }  
  
    public synchronized void close(Statement stmt, ResultSet rs){  
    	boolean isAuto = false;
    	Connection conn = null;
    	try {
    		if(rs != null){
    			rs.close();
        	}
    		if(stmt != null){
    			stmt.close();
        	}
    		PoolConnection pc = threadLocal.get();
    		if(pc != null){
    			conn = pc.getConnection();
    			isAuto = conn.getAutoCommit();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(isAuto){
    		close();
    	}
    }  
    
    
    public synchronized void close(){  
    	
    	synchronized (this) {
    		PoolConnection conn = threadLocal.get();
    		if(conn == null){
    			return;
    		}
    		
    		if(conn.getTransactional() > 0){
    			return;
    		}
    		
        	int free = 0;
        	for(PoolConnection pc : connections){
        		
        		if(pc == conn){
        			pc.setActive(false);
        		}
        		if(!pc.isActive()){
        			free++;
        		}
        	}
            threadLocal.remove(); 
            
            
        	int size = connections.size();
            if(free > LEFT){
            	atomic.getAndIncrement();
            }else{
            	atomic.set(0);
            }
            if(atomic.get() > TIMES &&  free > info.getMinActive()){
            	for(int i = size - 1; i >= size - LEFT ;i--){
            		PoolConnection pc = connections.get(i);
            		if(!pc.isActive()){
            			try {
            				pc.getConnection().close();
    					} catch (Exception e) {
    					}
            			connections.remove(i);
            		}
            	}
            }
            notifyAll();  
		}
    }  
    
    public void beginTransaction(){
    	PoolConnection conn = threadLocal.get();
    	if(conn == null){
    		return;
    	}
    	try {
    		conn.increase();
        	if(conn.getConnection().getAutoCommit()){
        		conn.getConnection().setAutoCommit(false);
        	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void commitTransaction(){
    	PoolConnection conn = threadLocal.get();
    	if(conn == null){
    		return;
    	}
    	try {
        	if(!conn.getConnection().getAutoCommit() && (conn.getTransactional() == 1)){
        		conn.decrease();
        		conn.getConnection().commit();
        		conn.getConnection().setAutoCommit(true);
        	}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
    }
    
    public void rollbackTransaction(){
    	PoolConnection conn = threadLocal.get();
    	if(conn == null){
    		return;
    	}
    	try {
        	if(!conn.getConnection().getAutoCommit() && (conn.getTransactional() <= 1)){
        		conn.getConnection().rollback();
        		conn.getConnection().setAutoCommit(true);
        	}
        	conn.decrease();

		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
   
}