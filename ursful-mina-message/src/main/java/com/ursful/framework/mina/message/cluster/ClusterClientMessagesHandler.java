package com.ursful.framework.mina.message.cluster;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.message.server.IClusterServerMessage;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClusterClientMessagesHandler implements ClientPacketHandler {

    private static Logger logger = LoggerFactory.getLogger(ClusterClientMessagesHandler.class);

    public int opcode() {
        return Opcode.MESSAGE.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String type = reader.readString();
        String fromCid = reader.readString();
        String toCid = reader.readString();
        byte[] data = reader.readBytes();// Message内容

        List<IClusterServerMessage> serverMessages = UrsManager.getObjects(IClusterServerMessage.class);
        if(serverMessages.size() > 0) {
            Message message = Message.parseMessage(data);
            for (IClusterServerMessage serverMessage : serverMessages) {
                if (serverMessage.received(message, writer)) {
                    //done
                    return;
                }
            }
        }

        if(Message.BROADCAST.equals(type)){//send to everybody.
            ClientManager.broadcastClients(new ByteArrayPacket(data));
            return;
        }
        Client client = ClientManager.getClient(toCid);
        if(client != null){
            client.write(new ByteArrayPacket(data));
        }else{
            logger.info("Type:" + type +  ", no client : " + toCid + " from: " + fromCid);
        }
    }


}