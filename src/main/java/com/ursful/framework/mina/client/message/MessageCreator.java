package com.ursful.framework.mina.client.message;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteWriter;

/**
 * 类名：MessageCreator
 * 创建者：huangyonghua
 * 日期：2019/3/1 15:41
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class MessageCreator {

    public static Packet createMessage(Message message) {
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.MESSAGE.ordinal());
        writer.writeString(message.getId());
        writer.writeString(message.getFromCid());
        writer.writeString(message.getToCid());
        writer.writeShort(message.getType());//cmd
        if(message.getData() != null) {
            writer.writeObject(message.getData());
        }
        return writer.getPacket();
    }


}
