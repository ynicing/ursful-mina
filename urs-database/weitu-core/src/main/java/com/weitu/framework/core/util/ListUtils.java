package com.weitu.framework.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：ListUtils
 * 创建者：huangyonghua
 * 日期：2017-10-17 17:35
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ListUtils {

    public static String join(String[] words, String key){
        StringBuffer sb = new StringBuffer();
        for(String word : words){
            if(sb.length() == 0){
                sb.append(word);
            }else{
                sb.append(key + word);
            }
        }
        return sb.toString();
    }

    public static String join(List<String> words, String key){
        StringBuffer sb = new StringBuffer();
        for(String word : words){
            if(sb.length() == 0){
                sb.append(word);
            }else{
                sb.append(key + word);
            }
        }
        return sb.toString();
    }

    public static <T> List<T> asList(T [] ts){
        List<T> temp = new ArrayList<T>();
        if(ts == null){
            return null;
        }
        for(T t : ts){
            temp.add(t);
        }
        return temp;
    }

    public static <T> List<T> newList(T ... ts){
        List<T> temp = new ArrayList<T>();
        if(ts == null){
            return null;
        }
        for(T t : ts){
            temp.add(t);
        }
        return temp;
    }
}
