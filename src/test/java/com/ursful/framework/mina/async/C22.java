package com.ursful.framework.mina.async;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.IMessage;
import com.ursful.framework.mina.client.message.Message;
import com.ursful.framework.mina.client.message.MessageSession;
import com.ursful.framework.mina.common.InterfaceManager;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class C22 {
    public static void main(String[] args) throws Exception{

        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getData());
                Message newMsg = new Message();
                newMsg.setFromCid(message.getToCid());
                newMsg.setToCid(message.getFromCid());
                newMsg.setData("check:" + message.getData());
                System.out.println("发送：check");
                session.sendMessage(newMsg);
                Message msg =  message.reply("reply:" + message.getData());
                session.sendMessage(msg);
            }
        });
        UrsClient client = new UrsClient("client2", "127.0.0.1", 9091);
        client.run();
    }
}
