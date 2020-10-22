package com.ursful.framework.mina.message.client;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.message.MessageManager;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientMessagesHandler implements ClientPacketHandler {

    private Logger logger = LoggerFactory.getLogger(ClientMessagesHandler.class);

    public int opcode() {
        return Opcode.MESSAGE.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        Message message = Message.parseMessage(reader);
        logger.info("Client Messages:" + message.toString());
        if(!message.getId().startsWith("reply-")) {//不是回复类消息
            List<IMessage> messages = MessageManager.getManager().getMessages();
            for (IMessage imsg : messages) {
                ThreadUtils.start(new Runnable() {
                    @Override
                    public void run() {
                        imsg.message(message, new MessageSession(writer));
                    }
                });
            }
        }else{
            MessageReader.getReader().addMessage(message);
        }
    }

}