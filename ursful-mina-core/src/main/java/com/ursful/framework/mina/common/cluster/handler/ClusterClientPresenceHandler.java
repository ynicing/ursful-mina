package com.ursful.framework.mina.common.cluster.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.server.mina.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.OtherServerClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ClusterClientPresenceHandler implements ClientPacketHandler {

    private static Logger logger = LoggerFactory.getLogger(ClusterClientPresenceHandler.class);

    public int opcode() {
        return Opcode.PRESENCE.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String cid = reader.readString();
        int online = reader.readByte();
        Map<String, Object> data = reader.readObject();
        ClientInfo info = new ClientInfo(cid, online == 1, data);
        if("CLIENT".equalsIgnoreCase((String)data.get("client_type"))) {
            Packet packet = PacketCreator.getPresence(info);
            ClientManager.broadcastWithoutServer(packet);//转发，本地所有客户端。
            logger.info("Broadcast client online : " + (online == 1));
        } else {
            logger.info("Server client online : " + (online == 1));
        }
        if(info.getOnline()) {
            OtherServerClientManager.register(info);
        }else{
            OtherServerClientManager.deregister(info);
        }
    }

}