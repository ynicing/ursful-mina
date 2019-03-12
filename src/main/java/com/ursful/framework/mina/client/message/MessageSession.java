package com.ursful.framework.mina.client.message;

import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.support.Session;
import org.apache.mina.core.session.IoSession;

/**
 * 类名：Session
 * 创建者：huangyonghua
 * 日期：2019/3/1 15:48
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class MessageSession extends Session{

    public MessageSession(PacketWriter session){
        super(session);
    }

    public void sendMessage(Message message){
        send(MessageCreator.createMessage(message));
    }

    public Message getReply(Message message, long millisecond){
        MessageCollector response = MessageCenter.createPacketCollector("reply-" + message.getId());
        sendMessage(message);
        // Wait up to a certain number of seconds for a reply
        Message result = response.nextResult(millisecond);
        // Stop queuing results
        response.cancel();
        return result;
    }


}
