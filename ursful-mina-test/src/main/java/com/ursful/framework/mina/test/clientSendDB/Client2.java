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
public class Client2 {
    public static void main(String[] args) throws Exception{

        UrsManager.register(new IClientStatus() {
            @Override
            public void clientReady(UrsClient client, String cid) {
                MessageManager.getManager().setClient(client);
                System.out.println("client:" + client);
                //给client1 发送消息
                Message message = new Message();
                message.setFromCid(cid);
                message.setToCid("client1@server");
                message.put("text","hello");
                System.out.println(String.format("1.发送消息：%s  from：%s to：%s",  message.getData(), message.getFromCid(), message.getToCid()));
                Message reply = MessageManager.getServerReply(message, 3000);
                System.out.println(String.format("6.接收到服务端确认了：%s", reply.getData()));
            }

            @Override
            public void clientClose(UrsClient client, String cid) {

            }
        });

        UrsClient client = new UrsClient("client2", "127.0.0.1", 19092);
        System.out.println("client:" + client);
        client.register(new ClientMessagesHandler());
        new Thread(client).run();


    }
}
