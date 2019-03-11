package com.ursful.framework.mina;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.common.InterfaceManager;

import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class C {
    public static void main(String[] args) throws Exception{


        UrsClient client = new UrsClient("all", "192.168.102.97", 9090);
        client.run();

//        client.close();


//        Thread.sleep(3*1000);
//        message.setType(1);
//        message.setData("new test");
//        client.getMessageSession().sendMessage(message);
//        System.out.println("end.");
    }
}
