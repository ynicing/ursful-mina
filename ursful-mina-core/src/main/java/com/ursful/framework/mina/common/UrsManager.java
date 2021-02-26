package com.ursful.framework.mina.common;

import com.ursful.framework.mina.common.support.IOrder;

import java.util.*;

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
        // sort...
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
                List<IOrder> orders = new ArrayList<IOrder>();
                List<Object> temp = new ArrayList<Object>();
                for(Object obj : objects){
                    if (obj instanceof IOrder){
                        orders.add((IOrder)obj);
                    }else{
                        temp.add(obj);
                    }
                }
                orders.sort(new Comparator<IOrder>() {
                    @Override
                    public int compare(IOrder o1, IOrder o2) {
                        return o1.order() - o2.order();
                    }
                });
                temp.addAll(0, orders);
                interfaces.put(clazz, temp);
            }
        }
    }

    public static  class Test1 implements IOrder{
        public int order(){
            return 1;
        }
    }
    public static  class Test2 implements IOrder{
        public int order(){
            return 3;
        }
    }
    public static void main(String[] args) {
        register(new Test1());
        register(new Test2());
        System.out.println(getObjects(IOrder.class));
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
