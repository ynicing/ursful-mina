package com.ursful.framework.mina.common.tools;

import java.util.Collection;
import java.util.Random;

/**
 * 类名：StringUtils
 * 创建者：huangyonghua
 * 日期：2019/3/5 9:22
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class StringUtils {

    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    private static Random randGen = new Random();

    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char [] randBuffer = new char[length];
        for (int i=0; i<randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    public static String join(Collection<String> list){
        StringBuffer stringBuffer = new StringBuffer();
        for(String c : list){
            if(!"".equals(c)){
                if(stringBuffer.length() == 0){
                    stringBuffer.append(c);
                }else{
                    stringBuffer.append("," + c);
                }
            }
        }
        return stringBuffer.toString();
    }
}
