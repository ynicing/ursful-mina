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
package com.weitu.framework.component.orm.query;

import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.framework.component.orm.annotation.RdTable;
import com.weitu.framework.component.orm.exception.QueryException;
import com.weitu.framework.component.orm.support.*;

import java.util.ArrayList;
import java.util.List;

public class BaseQueryImpl implements IBaseQuery {
	
	//private static IBaseDao<SysUser> baseDao = new BaseDaoImpl<SysUser>();
	
	//private QueryInfo info;
	//private TableInfo table;
    private Class<?> table;
    private Class<?> returnClass;
    private List<Column> returnColumns = new ArrayList<Column>();

	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Condition> havings = new ArrayList<Condition>();
	private List<Column> groups = new ArrayList<Column>();
	private List<Order> orders = new ArrayList<Order>();
	
	private boolean distinct = false;
	
	public IBaseQuery orderDesc(String name){
		orders.add(new Order(new Column(name), Order.DESC));
		return this;
	}

	public IBaseQuery where(String name, Object value, ExpressionType type){
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        conditions.add(new Condition().and(new Expression(new Column(name), value, type)));
		return this;
	}

	public IBaseQuery where(Terms terms) {
		conditions.add(terms.getCondition());
		return this;
	}


	public IBaseQuery group(String name) {
		groups.add(new Column(name));
		return this;
	}

	
	public IBaseQuery having(String name, Object value, ExpressionType type) {
        if(value == null){
            return this;
        }
        if("".equals(value)){
            return this;
        }
        havings.add(new Condition().or(new Expression(new Column(name), value, type)));

		return this;
	}

	public IBaseQuery having(Terms terms) {
		conditions.add(terms.getCondition());
		return this;
	}

	public IBaseQuery orderAsc(String name){
		orders.add(new Order(new Column(name), Order.ASC));
		return this;
	}

    @Override
    public IBaseQuery createQuery() throws QueryException {
        this.returnClass = this.table;
        return this;
    }



    @Override
    public IBaseQuery createQuery(String... names) throws QueryException {
        this.returnClass = this.table;
        if(names != null){
            for(String name : names){
                returnColumns.add(new Column(name));
            }
        }
        return this;
    }

    @Override
    public IBaseQuery createQuery(Column... columns) throws QueryException {
        this.returnClass = this.table;
        if(columns != null){
           for(Column column : columns){
               returnColumns.add(column);
           }
        }
        return this;
    }

    @Override
    public IBaseQuery distinct() {
        this.distinct = true;
        return this;
    }

    public IBaseQuery table(Class<?> clazz) throws QueryException{
        if(clazz != null) {
            RdTable rdTable = (RdTable)clazz.getAnnotation(RdTable.class);
            if(rdTable != null) {
                this.table = clazz;
            }
        }
		return this;
	}

    private QueryHelper helper = new QueryHelper();

    @Override
    public QueryHelper queryHelper() {
        helper.setConditions(conditions);
        helper.setGroups(groups);
        helper.setOrders(orders);
        helper.setTable(table);
        helper.setHavings(havings);
        helper.setReturnClass(returnClass);
        helper.setReturnColumns(returnColumns);
        helper.setDistinct(distinct);
        return helper;
    }
	 

}
