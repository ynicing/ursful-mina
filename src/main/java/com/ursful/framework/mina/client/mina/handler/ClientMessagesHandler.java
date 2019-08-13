package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;
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
    private BlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(500, true);

    public BlockingQueue<Message> getMessages() {
        return messages;
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String id = reader.readString();
        String fromCid = reader.readString();
        String toCid = reader.readString();
        int type = reader.readShort();
        Object data = null;
        if(reader.available() > 0){
            data = reader.readObject();
        }
        Message message = new Message();
        message.setId(id);
        message.setFromCid(fromCid);
        message.setToCid(toCid);
        message.setType(type);
        message.setData(data);
//        logger.info("Client Messages:" + message.toString());
        if(!id.startsWith("reply-")) {
            List<IMessage> messages = InterfaceManager.getObjects(IMessage.class);
            for (IMessage imsg : messages) {
                ThreadUtils.start(new Runnable() {
                    @Override
                    public void run() {
                        imsg.message(message, new MessageSession(writer));
                    }
                });
            }
        }else{
                try {
                    messages.put(message);
                }
                catch (InterruptedException ie) {
                    return;
                }
                synchronized (messages) {
                    messages.notifyAll();
                }
//            final Message msg = message;
//            Collection<MessageCollector> collectors =  MessageCenter.getPacketCollectors();
//            for(final  MessageCollector collector : collectors){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        collector.processPacket(msg);
//                    }
//                }).start();
//            }
        }
    }

//    static public Packet getReply(Connection connection, Packet packet, long timeout)
//            throws XMPPException
//    {
//        PacketFilter responseFilter = new PacketIDFilter(packet.getPacketID());
//        PacketCollector response = connection.createPacketCollector(responseFilter);
//
//        connection.sendPacket(packet);
//
//        // Wait up to a certain number of seconds for a reply.
//        Packet result = response.nextResult(timeout);
//
//        // Stop queuing results
//        response.cancel();
//
//        if (result == null) {
//            throw new XMPPException("No response from server.");
//        }
//        else if (result.getError() != null) {
//            throw new XMPPException(result.getError());
//        }
//        return result;
//    }


}