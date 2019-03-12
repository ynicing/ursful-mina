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
public class C2 {
    public static void main(String[] args) throws Exception{
        System.out.println("MAIN>>" + Thread.currentThread().getName());
        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(">c2>>>>>message>>>>>>>>>" + Thread.currentThread().getName());
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getData());
                Message newMsg = new Message();
                newMsg.setFromCid(message.getToCid());
                newMsg.setToCid(message.getFromCid());
                newMsg.setData("check:" + message.getData());
                System.out.println("发送：check");
                Message reply = session.getReply(newMsg, 10000);
                if(reply != null){
                    Message msg =  message.reply("reply:" + message.getData());
                    session.sendMessage(msg);
                    System.out.println("发送reply");
                }else{
                    System.out.println("未收到 check");
                }

            }
        });
        UrsClient client = new UrsClient("client2", "127.0.0.1", 9091);
        client.run();
    }
}
