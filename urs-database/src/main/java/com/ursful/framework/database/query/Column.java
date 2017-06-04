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

public class Column{
	
	private String name;
	private String function;
	private String alias;
	private String asName;
	
	public String getAsName() {
		return asName;
	}

	public void setAsName(String asName) {
		this.asName = asName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	//---num
	//count(num)
	//count(num) as n
	//x count(a.num)
	//--a.num
	//--a.num as n
	//--count(a.num) as n
	
	//count(num) as n
	/*public Column(StringBuffer function, String name, String asName){
		this.function = function.toString();
		this.name = name;
		this.asName = asName;
	}*/
 
	//num
	public Column(String name){
		this.name = name;
	}
		
	//u.num
	public Column(String alias, String name){
		this.name = name;
		this.alias = alias;
	}
		
	//u.num as big
	public Column(String alias, String name, String asName){
		this.alias = alias;
		this.name = name;
		this.asName = asName;
	}
	
	//sum(u.num) as big
	public Column(String function, String alias, String name, String asName){
		this.function = function;
		this.alias = alias;
		this.name = name;
		this.asName = asName;
	}

}