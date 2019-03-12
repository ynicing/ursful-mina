package com.ursful.framework.mina.async;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.InterfaceManager;

import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class C1 {
    public static void main(String[] args) throws Exception{

        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(">message>>>>>>>>>>>>>>" + Thread.currentThread().getName());
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getData());
                Message msg = message.reply("reply:" + message.getData());
                session.sendMessage(msg);
                System.out.println("Client2发送reply");

            }
        });
        UrsClient client = new UrsClient("client1", "127.0.0.1", 9090);
        client.run();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(">run>>>>>>>>>>>>>>" + Thread.currentThread().getName());
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                }
                Message newMsg = new Message();
                newMsg.setFromCid(client.getCid());
                newMsg.setToCid("client2@server2");
                newMsg.setData("source");
                System.out.println("发送：source");

                Message reply = client.getMessageSession().getReply(newMsg, 20*1000);
                System.out.println("》》》》" + reply);
            }
        }).start();
    }
}
