package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.presence.IPresenceInfo;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientPresenceInfoHandler implements ClientPacketHandler{

    public int opcode() {
        return Opcode.PRESENCE_INFO.ordinal();
    }


    public void handlePacket(ByteReader reader, PacketWriter writer) {
        boolean isTransfer = reader.readByte() == 1;
        List<ClientInfo> clientInfos = new ArrayList<ClientInfo>();
        while (reader.available() > 0){
            String cid = reader.readString();
            int online = reader.readByte();
            Map<String, Object> data = reader.readObject();
            ClientInfo info = new ClientInfo(cid, online == 1, data);
            info.setTransfer(isTransfer);
            clientInfos.add(info);
        }

        List<IPresenceInfo> presenceInfos = UrsManager.getObjects(IPresenceInfo.class);
        for(IPresenceInfo info : presenceInfos) {
            info.presences(isTransfer, clientInfos);
        }
    }

}