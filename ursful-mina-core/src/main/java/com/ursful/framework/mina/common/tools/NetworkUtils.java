package com.ursful.framework.mina.common.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 类名：NetworkUtils
 * 创建者：huangyonghua
 * 日期：2019/2/28 9:13
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class NetworkUtils {

    public static List<String> getHostAddress(){
        List<String> temp = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> ints = NetworkInterface.getNetworkInterfaces();
            while(ints.hasMoreElements()){
                NetworkInterface face = ints.nextElement();
                Enumeration<InetAddress> addrs = face.getInetAddresses();
                while(addrs.hasMoreElements()){
                    InetAddress addr = addrs.nextElement();
                    String ad = addr.getHostAddress();
                    if(!temp.contains(ad) && (ad.split("[.]").length == 4)){
                        temp.add(ad);
                    }
                }
            }
            if(temp.size() > 1){
                temp.remove("127.0.0.1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
}
