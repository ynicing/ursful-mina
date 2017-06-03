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