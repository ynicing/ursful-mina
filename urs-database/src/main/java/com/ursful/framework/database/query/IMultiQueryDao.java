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

public interface IMultiQueryDao<T> extends IQuery<T>{
	 
	IMultiQueryDao<T> createAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> createLeftAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> createRightAliasTable(String alias, Class<?> clazz) throws QueryException;
	IMultiQueryDao<T> group(Column column);
	IMultiQueryDao<T> having(Column left, Object value, ConditionType type);
	IMultiQueryDao<T> on(Column left, Column right);
	IMultiQueryDao<T> where(Column left, Object value, ConditionType type);
	IMultiQueryDao<T> where(Column left, Column value);
	IMultiQueryDao<T> orderDesc(Column column);
	IMultiQueryDao<T> orderAsc(Column column);
	
}
