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

public interface IQueryDao<T> extends IQuery<T>{
	 
	//从class中获取字段，该字段可有可无
	IQueryDao<T> table(Class<?> clazz) throws QueryException;
	IQueryDao<T> where(String name, Object value, ConditionType type);
	IQueryDao<T> group(String name);
	IQueryDao<T> having(String name, Object value, ConditionType type);
	IQueryDao<T> orderDesc(String name);
	IQueryDao<T> orderAsc(String name);
	IQueryDao<T> createQuery(String... names) throws QueryException;
	IQueryDao<T> createQuery(Column... columns) throws QueryException;

}
