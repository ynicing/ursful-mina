package com.ursful.framework.mina.test.clientSendForCheck;


import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.message.MessageManager;
import com.ursful.framework.mina.message.client.ClientMessagesHandler;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;

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
                Message msg = message.reply();
                msg.put("abc", "client1-" + message.getData().get("zzz"));
                session.sendMessage(msg);
            }
        });

        UrsClient client = new UrsClient("client1", "127.0.0.1", 9090);

        client.register(new ClientMessagesHandler());

        new Thread(client).run();

        Thread.sleep(3000);

        Message message = new Message();
        message.setType(Message.CHAT);
        message.setFromCid(client.getCid());
        message.setToCid("client2@" + client.getServerId());
        message.put("x", "hello check...");
        message.put("y",  123);

        MessageManager.getManager().setClient(client);

        Message reply = MessageManager.getManager().getReply(message, 1000);
        System.out.println("now:" + reply.getData());

    }
}
