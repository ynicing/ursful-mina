package com.ursful.framework.mina.test.clientSend2Client;


import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.IPresence;
import com.ursful.framework.mina.client.message.IPresenceInfo;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.message.MessageManager;
import com.ursful.framework.mina.message.client.ClientMessagesHandler;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;

import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Client2 {
    public static void main(String[] args) throws Exception{

        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());
                Message reply = message.reply();
                reply.put("anx", "hello");
                session.sendMessage(reply);
            }
        });

        UrsManager.register(new IPresence() {
            @Override
            public void presence(String cid, boolean online, Map<String, Object> data) {
                System.out.println("user[" + cid + "]:" + (online ? "Online" : "Offline") + " >>> " + data);
            }
        });

        UrsManager.register(new IPresenceInfo() {
            @Override
            public void presences(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        UrsManager.register(new IClientStatus() {

            @Override
            public void clientReady(String cid) {
                System.out.println("clientReady:" + cid);
            }

            @Override
            public void clientClose(String cid) {
                System.out.println("clientClose:" + cid);
            }
        });

        UrsClient client = new UrsClient("client1", "127.0.0.1", 19090);

        client.register(new ClientMessagesHandler());

        new Thread(client).run();

        Thread.sleep(3*1000);
        Message message = new Message();
        message.setType(Message.CHAT);
        message.setFromCid(client.getCid());
        message.setToCid("client2@" + client.getServerId());
        message.put("x", "hello test2.");
        message.put("y",  123);

        MessageManager.getManager().setClient(client);
        Message reply = MessageManager.getManager().getReply(message, 1000);
        System.out.println("now:" + reply.getData());

    }
}
