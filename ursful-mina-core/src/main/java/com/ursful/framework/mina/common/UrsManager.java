package com.ursful.framework.mina.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名：UrsManager
 * 创建者：huangyonghua
 * 日期：2019/2/28 14:14
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class UrsManager {
    private static Map<Class, List<Object>> interfaces = new HashMap<Class, List<Object>>();

    public static <T> List<T> getObjects(Class clazz){
        List<T> list = new ArrayList<T>();
        List<T> temp = (List<T>)interfaces.get(clazz);
        if(temp != null){
            list.addAll(temp);
        }
        return list;
    }

    public static void register(Object object){
        Class [] classes = object.getClass().getInterfaces();
        for(Class clazz : classes){
            List<Object> objects = interfaces.get(clazz);
            if(objects == null){
                objects = new ArrayList<Object>();
            }
            if(!objects.contains(object)){
                objects.add(object);
                interfaces.put(clazz, objects);
            }
        }
    }

    public static void deregister(Object object){
        Class [] classes = object.getClass().getInterfaces();
        for(Class clazz : classes){
            List<Object> objects = interfaces.get(clazz);
            if(objects != null){
                if(objects.contains(object)){
                    objects.remove(object);
                    interfaces.put(clazz, objects);
                }
            }
        }
    }
}
