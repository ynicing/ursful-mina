package com.ursful.framework.mina.test.clientSend2Servers;


import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.presence.IPresence;
import com.ursful.framework.mina.client.presence.IPresenceInfo;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.message.MessageManager;
import com.ursful.framework.mina.message.client.ClientMessagesHandler;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;

import java.util.List;
import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Client1 {
    public static void main(String[] args) throws Exception{

        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                Message reply = message.reply();
                reply.put("an", "hello");
                session.sendMessage(reply);
            }
        });

        UrsManager.register(new IPresence() {
            @Override
            public void presence(ClientInfo info) {
                System.out.println("user[" + info.getCid() + "]:" + (info.getOnline() ? "Online" : "Offline") + " >>> " + info.getData());
            }
        });

        UrsManager.register(new IPresenceInfo() {
            @Override
            public void presences(boolean isTransfer, List<ClientInfo> cids) {
                System.out.println("presence info : " + cids);
            }
        });

        UrsManager.register(new IClientStatus() {

            @Override
            public void clientReady(UrsClient client, String cid) {
                System.out.println("clientReady:" + cid);
            }

            @Override
            public void clientClose(UrsClient client, String cid) {
                System.out.println("clientClose:" + cid);
            }
        });

        UrsClient client = new UrsClient("client2", "127.0.0.1", 19091);

        client.register(new ClientMessagesHandler());

        new Thread(client).run();


    }
}
