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

package com.ursful.framework.database.query;



import com.ursful.framework.database.page.Page;


public interface IQuery<T> {
	IQuery<T> createDistinctQuery(Class<?> clazz, Column... columns) throws QueryException;
	IQuery<T> createQuery(Class<?> clazz, Column... columns) throws QueryException;//select a.id, a.name from
	IQuery<T> createCount() throws QueryException;;//select count(*) from.
	IQuery<T> createCount(Column column) throws QueryException;//select count(a.id) from...
	IQuery<T> createDistinctString(Column column) throws QueryException;
	IQuery<T> createPage(Page page) throws QueryException;// page...
	QueryInfo getQueryInfo();
	//由createQuery决定
	//List<T> query() throws QueryException;
	//int queryCount() throws QueryException;
	//Page queryPage(Page page) throws QueryException;
	
	//query group, query distinct
	//query other?
}
