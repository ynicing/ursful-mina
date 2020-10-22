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
public class Client1 {
    public static void main(String[] args) throws Exception{


        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                Message msg = message.reply();

                Message m = new Message();
                m.setType(Message.CHAT);
                m.setToCid(message.getFromCid());
                m.setFromCid(message.getToCid());
                m.put("zzz", "client2");

                Message reply = session.getReply(m, 1000);
                if(reply != null) {
                    msg.put("result", reply.getData().get("abc"));
                }
                session.sendMessage(msg);
            }
        });



        UrsClient client = new UrsClient("client2", "127.0.0.1", 9090);

        client.register(new ClientMessagesHandler());

        new Thread(client).run();


    }
}
