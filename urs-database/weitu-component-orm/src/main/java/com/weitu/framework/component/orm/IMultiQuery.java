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


import com.weitu.framework.component.orm.exception.QueryException;
import com.weitu.framework.component.orm.support.*;

public interface IMultiQuery extends IQuery{

    //IMultiQuery createDistinctQuery(Class<?> clazz, Column... columns) throws QueryException;
    IMultiQuery createQuery(Class<?> clazz, Column... columns) throws QueryException;//select a.id, a.name from
    //IMultiQuery createCount() throws QueryException;;//select count(*) from.
    //IMultiQuery createCount(Column column) throws QueryException;//select count(a.id) from...
    //IMultiQuery createDistinctString(Column column) throws QueryException;
    //IMultiQuery createPage(Page page) throws QueryException;// page...
    //QueryInfo getQueryInfo();

    IMultiQuery table(String alias, Class<?> clazz) throws QueryException;
    IMultiQuery where(Column left, Object value, ExpressionType type);
    IMultiQuery where(Column left, Column value);
    IMultiQuery where(Condition condition);
    IMultiQuery group(Column ...column);
    IMultiQuery having(Column left, Object value, ExpressionType type);
    IMultiQuery having(Column left, Column value);
    IMultiQuery having(Condition condition);
    IMultiQuery orderDesc(Column column);
    IMultiQuery orderAsc(Column column);
    IMultiQuery join(Join join) throws QueryException;
    IMultiQuery distinct();



}
