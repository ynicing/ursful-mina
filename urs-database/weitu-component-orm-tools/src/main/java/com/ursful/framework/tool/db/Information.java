package com.ursful.framework.tool.db;

public class Information{

    private String db;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public Information(String type){
        this.type = type;
        if ("mysql".equalsIgnoreCase(type)){
            this.testSQL = "select 1";
        }else if("oracle".equalsIgnoreCase(type)){
            this.testSQL = "select 1 from dual";
        }else{
            this.testSQL = "select 1";
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String driver;
    private String url;
    private String username;
    private String password;
    private String testSQL;
    private String ip;
    private String type;
    private String port;

    public String getPort() {
        if(port == null) {
            if ("mysql".equalsIgnoreCase(this.type)) {
                this.port = "3306";
            } else if ("oracle".equalsIgnoreCase(this.type)) {
                this.port = "1521";
            }
        }
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private String schema;


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTestSQL() {
        return testSQL;
    }

    public void setTestSQL(String testSQL) {
        this.testSQL = testSQL;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getDriver() {
        if("mysql".equalsIgnoreCase(this.type)){
            this.driver = "com.mysql.jdbc.Driver";
        }else if("oracle".equalsIgnoreCase(this.type)){
            this.driver = "oracle.jdbc.driver.OracleDriver";
        }
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getUrl() {
        if("mysql".equalsIgnoreCase(this.type)){
            if(db != null){
                this.url = "jdbc:mysql://" + ip + ":" + getPort() + "/" + db + "?useUnicode=true&characterEncoding=utf-8";
            }else {
                this.url = "jdbc:mysql://" + ip + ":" + getPort() + "";
            }
        }else if("oracle".equalsIgnoreCase(this.type)){
            this.url = "jdbc:oracle:thin:@" + ip + ":" + getPort() + ":" + this.schema;
        }
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String toString(){
        return this.type + "," + this.driver + "," + this.url + ","
                + this.username + "," + this.password + ", " + this.testSQL + "," + this.port;
    }
}