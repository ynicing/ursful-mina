package com.ursful.framework.mina.common.cluster.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.mina.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.OtherServerClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ClusterClientPresenceHandler implements ClientPacketHandler {

    private static Logger logger = LoggerFactory.getLogger(ClusterClientPresenceHandler.class);

    public int opcode() {
        return Opcode.PRESENCE.ordinal();
    }

    /**
     * 当server2的新用户的在线信息转发给server1
     * @param reader
     * @param writer
     */
    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String cid = reader.readString();// 当前为server1， test2 登录 server2， 那么， cid = test2@server2
        int online = reader.readByte();
        Map<String, Object> data = reader.readObject();
        ClientInfo info = new ClientInfo(cid, online == 1, data);
        if(!info.isServer()) {
            Packet packet = PacketCreator.getPresence(info);
            ClientManager.broadcastClients(packet);//转发，本地所有客户端。
            logger.info("Broadcast client online : " + (online == 1));
            ClientManager.updateClientInfo(User.getDomain(cid), cid, online == 1, data);
            List<IClientManager> list = UrsManager.getObjects(IClientManager.class);
            for(IClientManager manager : list){
                manager.registerServerClient(info);
            }
        } else {
            //server-client 忽略
            logger.info("Server client online : " + (online == 1));
        }
    }

}