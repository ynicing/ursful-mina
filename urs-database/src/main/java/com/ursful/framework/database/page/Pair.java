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

package com.ursful.framework.database.page;

import java.io.Serializable;

public class Pair<T> implements Serializable{
	 
	private static final long serialVersionUID = 617587755877055322L;
	
	private String name;
    private String type;
	private T value;
	
	public Pair(String name, String type, T value){
		this.name = name;
        this.type = type;
		this.value = value;
	}

    public Pair(String name, T value){
        this.name = name;
        this.value = value;
        if(value != null){
            this.type = value.getClass().getSimpleName();
        }
    }
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
		if(name == null){
			return value + "";
		}
		return "(" + name + "," + value + ")";
	}
	
}
