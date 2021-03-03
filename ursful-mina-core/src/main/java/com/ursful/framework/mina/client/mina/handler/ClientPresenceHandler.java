package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.presence.IPresence;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;

import java.util.List;
import java.util.Map;

public class ClientPresenceHandler implements ClientPacketHandler {

    public int opcode() {
        return Opcode.PRESENCE.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String cid = reader.readString();
        int online = reader.readByte();// 0 offline, 1 online
        int changed = reader.readByte();// 0 --  1 changed
        Map<String, Object> data = reader.readObject();
        ClientInfo info = new ClientInfo(cid, online == 1, changed == 1,  data);
        List<IPresence> presenceInfos = UrsManager.getObjects(IPresence.class);
        for(IPresence presence : presenceInfos){
            if(info.isChanged()) {
                presence.metaDataChange(info);
            }else{
                presence.presence(info);
            }
        }
    }

}