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

import java.util.List;
import java.util.Map;

public interface IBaseSQL {

    String queryLatestVersion(String moduleName);

    Object queryObject(String ql, Object... parameters);

    List<String> getCurrentTables();

    List<Object> query(String sql, Object... parameters);
    List<Map<String, Object>> queryMap(String sql, Object... parameters);

    int execute(String sql);
    int execute(String sql, Object... parameters);
    void executeBatch(String sql, Object[][] parameters);

}
