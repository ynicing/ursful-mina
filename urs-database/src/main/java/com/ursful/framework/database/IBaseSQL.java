package com.ursful.framework.database;


import java.util.List;
import java.util.Map;


public interface IBaseSQL {

    //void saveModule(Module module);

    String queryLatestVersion(String moduleName);

    Object queryObject(String ql, Object... parameters);

    List<String> getCurrentTables();

    List<Object> query(String sql, Object... parameters);
    List<Map<String, Object>> queryMap(String sql, Object... parameters);

   // List<Object> query(String sql, Object [] parameters);
   // List<Map<String, Object>> queryMap(String sql, Object [] parameters);

    int execute(String sql);
    int execute(String sql, Object... parameters);
    void executeBatch(String sql, Object[][] parameters);

}
