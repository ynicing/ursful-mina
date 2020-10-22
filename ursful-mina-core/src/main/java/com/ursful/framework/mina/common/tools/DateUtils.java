package com.ursful.framework.mina.common.tools;

import java.util.Calendar;

/**
 * 类名：DateUtils
 * 创建者：huangyonghua
 * 日期：2019/2/27 13:37
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DateUtils {
    public static long getTimeMillisToNextDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }
}
