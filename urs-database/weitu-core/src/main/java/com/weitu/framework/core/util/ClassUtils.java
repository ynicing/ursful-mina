package com.weitu.framework.core.util;

/**
 * 类名：ClassUtils
 * 创建者：huangyonghua
 * 日期：2017-10-22 17:29
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClassUtils {

    public static String classToCode(Class<?> clazz){
        if(clazz == null){
            return null;
        }
        String fullName = clazz.getName();
        String [] names = fullName.split("[.]");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < names.length; i++){
            if(i == names.length - 1){
                if(sb.length() != 0) {
                    sb.append(".");
                }
                sb.append(names[i]);
            }else{
                sb.append(names[i].substring(0, 1));
            }
        }
        /*
        try {
            System.out.println(sb);
            byte [] bytes = sb.toString().getBytes("UTF-8");
            StringBuffer buffer = new StringBuffer();
            for(byte b : bytes){
                buffer.append(Integer.toHexString((0x000000ff&b) | 0xFFFFFF00).substring(6));
            }
            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        return sb.toString();
    }

}
