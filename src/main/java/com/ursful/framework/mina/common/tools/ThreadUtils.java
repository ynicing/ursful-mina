package com.ursful.framework.mina.common.tools;

/**
 * 类名：ThreadUtils
 * 创建者：huangyonghua
 * 日期：2019/3/11 14:25
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ThreadUtils {
    public static void start(Runnable runnable){
        new Thread(runnable).start();

    }
}
