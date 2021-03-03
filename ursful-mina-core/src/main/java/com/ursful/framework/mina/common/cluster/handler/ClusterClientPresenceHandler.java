package com.ursful.framework.mina.common.cluster.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.cluster.presence.IClusterPresence;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteReader;

import java.util.List;
import java.util.Map;

public class ClusterClientPresenceHandler implements ClientPacketHandler {

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
        int changed = reader.readByte();
        Map<String, Object> data = reader.readObject();
        ClientInfo info = new ClientInfo(cid, online == 1, changed == 1, data);

        List<IClusterPresence> presenceInfos = UrsManager.getObjects(IClusterPresence.class);
        for(IClusterPresence presence : presenceInfos){
            if(info.isChanged()){
                presence.metaDataChange(info);
            }else {
                presence.presence(info);
            }
        }
    }

}