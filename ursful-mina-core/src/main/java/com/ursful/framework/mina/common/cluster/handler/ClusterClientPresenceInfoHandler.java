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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClusterClientPresenceInfoHandler implements ClientPacketHandler{

    public int opcode() {
        return Opcode.PRESENCE_INFO.ordinal();
    }


    public void handlePacket(ByteReader reader, PacketWriter writer) {

        List<ClientInfo> infos = new ArrayList<ClientInfo>();
        while (reader.available() > 0){
            String cid = reader.readString();
//            int online = reader.readByte();
//            map.put(cid, online == 1);
            Map<String, Object> data = reader.readObject();
            ClientInfo info = new ClientInfo(cid, true, data);
            if("CLIENT".equals(data.get("CLIENT_TYPE"))) {
                infos.add(info);
            }
            OtherServerClientManager.register(info);
        }
        Packet packet = PacketCreator.getPresenceInfo(infos, true);
        ClientManager.broadcastWithoutServer(packet);//转发，本地所有客户端。

    }

}