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

import com.ursful.framework.database.annotaion.RsTransactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransactionHandler implements InvocationHandler {  
      
    private Object targetObject;  
      
    public Object newProxyInstance(Object targetObject){  
        this.targetObject = targetObject;  
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),  
        		targetObject.getClass().getInterfaces(), this);  
    }  
    
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{  
        Object ret = null;  
        Method m = targetObject.getClass().getMethod(method.getName(),
        		method.getParameterTypes());
        RsTransactional tran = m.getAnnotation(RsTransactional.class);
        boolean isTransactional = (tran != null);
        if(isTransactional){
        	
        	 try{  
        		 ConnectionManager.getManager().getConnection();
             	 ConnectionManager.getManager().beginTransaction();  
             	 ret = method.invoke(targetObject, args);   
                 ConnectionManager.getManager().commitTransaction();
             }catch(Exception e){  
                 ConnectionManager.getManager().rollbackTransaction();  
                 throw new RuntimeException(e);  
             }finally{  
                 ConnectionManager.getManager().close();  
             }  
        }else{
        	ret = method.invoke(targetObject, args);  
        }
       
        return ret;  
    }  
}  