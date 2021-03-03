package com.ursful.framework.mina.common.cluster.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.cluster.presence.IClusterPresenceInfo;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteReader;

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
            int online = reader.readByte();
            Map<String, Object> data = reader.readObject();
            ClientInfo info = new ClientInfo(cid, online == 1, data);
            infos.add(info);
        }

        List<IClusterPresenceInfo> presenceInfos = UrsManager.getObjects(IClusterPresenceInfo.class);
        for(IClusterPresenceInfo presence : presenceInfos){
            presence.presences(infos);
        }

    }

}