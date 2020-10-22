package com.ursful.framework.mina.message.cluster;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if(Message.BROADCAST.equals(type)){//send to everybody.
            ClientManager.broadcastWithoutServer(new ByteArrayPacket(data));
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