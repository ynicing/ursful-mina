package com.ursful.framework.mina.test.clientSendDB;

import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.message.server.MessagesHandler;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientManager;

import java.util.UUID;

/**
 * Created by ynice on 2020/10/14.
 */
public class ExtMessagesHandler extends MessagesHandler {

    @Override
    public void handlePacket(ByteReader reader, Client c) {
        Message message = Message.parseMessage(reader);
        if(message.getId().startsWith("reply-")){
            System.out.println("9.更新已收到消息，id:" +  message.get("databaseMessageId"));
        }else{

            System.out.println(String.format("2.保存消息：%s from: %s to :%s", message.getId(), message.getFromCid(), message.getToCid()));

            String databaseMessageId = UUID.randomUUID().toString();//保存后消息的id
            long databaseTimestamp = System.currentTimeMillis();//保存后消息的数据库时间
            System.out.println("3.保存数据库并获取一个id：" + databaseMessageId);

            //将消息回复给client1
            Message reply =  message.reply();
            reply.setFromCid("system");//服务端的确认
            reply.put("databaseMessageId", databaseMessageId);
            reply.put("databaseTimestamp", databaseTimestamp);
            System.out.println(String.format("4.回复告诉客户端消息我服务端收到了：%s from: %s to :%s", reply.getId(), reply.getFromCid(), reply.getToCid()));


            c.write(reply.getPacket());

            message.put("databaseMessageId", databaseMessageId);
            message.put("databaseTimestamp", databaseTimestamp);
            //将消息发送给client2
            Client client = ClientManager.getClient(message.getToCid());
            client.write(message.getPacket());
            System.out.println(String.format("5.服务端转发给目标用户：%s from: %s to :%s", message.getId(), message.getFromCid(), message.getToCid()));

        }
    }
}
