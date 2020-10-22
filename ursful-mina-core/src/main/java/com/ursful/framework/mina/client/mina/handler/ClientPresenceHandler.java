package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.message.IPresence;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
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
        int online = reader.readByte();
        Map<String, Object> data = reader.readObject();
        List<IPresence> presenceInfos = UrsManager.getObjects(IPresence.class);
        for(IPresence presence : presenceInfos){
            data.put("ONLINE", online == 1);
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    presence.presence(cid, online == 1, data);
                }
            });
        }
    }

}