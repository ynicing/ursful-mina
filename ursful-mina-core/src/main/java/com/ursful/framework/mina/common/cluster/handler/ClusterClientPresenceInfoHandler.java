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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClusterClientPresenceInfoHandler implements ClientPacketHandler{

    public int opcode() {
        return Opcode.PRESENCE_INFO.ordinal();
    }


    /**
     * 当server1 连接到server2 时， server2的在线信息转发给server1
     * @param reader
     * @param writer
     */
    public void handlePacket(ByteReader reader, PacketWriter writer) {

        List<ClientInfo> infos = new ArrayList<ClientInfo>();
        while (reader.available() > 0){
            String cid = reader.readString();
            Map<String, Object> data = reader.readObject();
            ClientInfo info = new ClientInfo(cid, true, data);
            if("CLIENT".equalsIgnoreCase((String)data.get("client_type"))) {
                infos.add(info);
                List<IClientManager> list = UrsManager.getObjects(IClientManager.class);
                for(IClientManager manager : list){
                    manager.registerServerClient(info);
                }
            }
            ClientManager.updateClientInfo(User.getDomain(cid), cid, true, data);
        }

        Packet packet = PacketCreator.getPresenceInfo(infos);
        ClientManager.broadcastClients(packet);//转发，本地所有客户端。

    }

}