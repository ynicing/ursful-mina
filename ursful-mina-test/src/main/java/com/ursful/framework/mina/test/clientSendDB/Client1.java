package com.ursful.framework.mina.test.clientSendDB;


import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.presence.IPresence;
import com.ursful.framework.mina.client.presence.IPresenceInfo;
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
public class Client1 {
    public static void main(String[] args) throws Exception{

        UrsManager.register(new IClientStatus() {
            @Override
            public void clientReady(UrsClient client, String cid) {
                MessageManager.getManager().setClient(client);
            }

            @Override
            public void clientClose(UrsClient client, String cid) {

            }
        });

        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                String databaseMessageId = message.get("databaseMessageId");
                System.out.println(String.format("7.收到消息了，%s：%s from:%s to:%s", databaseMessageId, message.getData(), message.getFromCid(), message.getToCid()));

                //返回给服务端，我已收到消息，下次再上线，就不会发送过来了。
                Message serverReply = message.reply();
                serverReply.put("databaseMessageId", databaseMessageId);
                serverReply.setToCid("system");
                session.getWriter().sendPacket(serverReply.getPacket());

                System.out.println(String.format("8.返回给服务端了，我收到了：%s from:%s to: %s", serverReply.getData(), serverReply.getFromCid(), serverReply.getToCid()));
            }
        });

        UrsClient client = new UrsClient("client1", "127.0.0.1", 19092);
        client.register(new ClientMessagesHandler());
        new Thread(client).run();


    }
}
