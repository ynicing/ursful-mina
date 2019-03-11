package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.message.IPresence;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import org.apache.mina.core.session.IoSession;

import java.util.List;
import java.util.Map;

public class ClientPresenceHandler implements ClientPacketHandler {

    public int opcode() {
        return Opcode.PRESENCE.ordinal();
    }

    public void handlePacket(ByteReader reader, IoSession session) {
        String cid = reader.readString();
        int online = reader.readByte();
        Map<String, Object> data = reader.readObject();
        List<IPresence> presenceInfos = InterfaceManager.getObjects(IPresence.class);
        for(IPresence presence : presenceInfos){
            data.put("ONLINE", online == 1);
            presence.presence(cid, online == 1, data);
        }
    }

}