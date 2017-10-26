package com.ursful.framework.tool.db;


public enum DatabaseType{
    MySQL,
    Oracle,
    SQLServer,
    UNKNOWN;

    public static DatabaseType getDatabaseType(String name){
        if(MySQL.name().equalsIgnoreCase(name)){
            return MySQL;
        }else if(Oracle.name().equalsIgnoreCase(name)){
            return Oracle;
        }else if(SQLServer.name().equalsIgnoreCase(name)){
            return SQLServer;
        }
        return UNKNOWN;
    }
}
