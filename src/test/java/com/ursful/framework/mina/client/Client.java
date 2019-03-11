package com.ursful.framework.mina.client;

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
public class Client {
    public static void main(String[] args) throws Exception{

        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getData());
            }
        });

        InterfaceManager.register(new IPresence() {
            @Override
            public void presence(String cid, boolean online, Map<String, Object> data) {
                System.out.println("[" + cid + "]:" + (online ? "Online" : "Offline") + " >>> " + data);
            }
        });

        InterfaceManager.register(new IPresenceInfo() {
            @Override
            public void presences(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        UrsClient client = new UrsClient("client", "127.0.0.1", 9093);
        new Thread(client).run();
        Thread.sleep(3*1000);

        Message message = new Message();
        message.setFromCid(client.getCid());
        message.setToCid("a@server1");
        message.setData("发给：a");
        message.setType(1);
        client.getMessageSession().sendMessage(message);
        System.out.println("发给：a");

        Thread.sleep(10*1000);
        message.setToCid("client10@server1");
        message.setData("发给：client10");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给：client10");


        Thread.sleep(10*1000);
        message.setToCid("client20@server2");
        message.setData("发给：client20");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给：client20");

        Thread.sleep(10*1000);
        message.setToCid("all@server1");
        message.setData("发给服务器server1");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给服务器server1");

        Thread.sleep(10*1000);
        message.setToCid("all@server2");
        message.setData("发给服务器server2");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给服务器server2");

        Thread.sleep(10*1000);
        message.setToCid("all@server3");
        message.setData("发给服务器server3");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给服务器server3");

        Thread.sleep(10*1000);
        message.setToCid("all@all");
        message.setData("发给所有服务器");
        client.getMessageSession().sendMessage(message);
        System.out.println("发给所有服务器");

    }
}
