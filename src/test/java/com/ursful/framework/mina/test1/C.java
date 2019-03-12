package com.ursful.framework.mina.test1;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.support.IClientStatus;

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

        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());
                if (message.getType() == 0) {
                    Message reply = message.reply("self reply.");
                    session.sendMessage(reply);

                    reply.setId(Message.nextID());
                    message.setType(1);
                    message.setData("new test");
                    session.sendMessage(message);
                } else if (message.getType() == 1) {
                    Message reply = message.reply("next reply");
                    reply.setId(Message.nextID());
                    reply.setType(2);
                    session.sendMessage(reply);
                } else {
                    System.out.println("type:" + message.getType());
                }
            }
        });

        InterfaceManager.register(new IPresence() {
            @Override
            public void presence(String cid, boolean online,  Map<String, Object> data) {
                System.out.println("one [" + cid + "]:" + (online?"Online":"Offline") + " >>> " + data);
            }
        });

        InterfaceManager.register(new IPresenceInfo() {
            @Override
            public void presences(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        InterfaceManager.register(new IClientStatus(){

            @Override
            public void clientReady(String cid) {
                System.out.println("clientReady:" + cid);
            }

            @Override
            public void clientClose(String cid) {
                System.out.println("clientClose:" + cid);
            }
        });

        UrsClient client = new UrsClient("client10", "127.0.0.1", 9090);
        new Thread(client).run();
        Thread.sleep(3*1000);
        Message message = new Message();
        message.setType(0);
        message.setFromCid(client.getCid());
        message.setToCid(client.getCid());
        message.setData("test");
        Message reply = client.getMessageSession().getReply(message, 1000);
        System.out.println("now:" + reply.getData());

        message.setFromCid(client.getCid());
        message.setToCid("system@" + client.getServerId());
        message.setData("servers");
        reply = client.getMessageSession().getReply(message, 1000);
        System.out.println("servers:" + reply);

        client.close();
//        client.close();


//        Thread.sleep(3*1000);
//        message.setType(1);
//        message.setData("new test");
//        client.getMessageSession().sendMessage(message);
//        System.out.println("end.");
    }
}
