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
        ThreadUtils.start(new Runnable() {
            @Override
            public void run() {
                List<IPresence> presenceInfos = UrsManager.getObjects(IPresence.class);
                for(IPresence presence : presenceInfos){
                    if(online == 1) {
                        data.put("online", true);
                        presence.presence(cid, true, data);
                    }else if(online == 0){
                        data.put("online", false);
                        presence.presence(cid, false, data);
                    }else if(online == 2){//change
                        presence.presenceChange(cid, data);
                    }
                }
            }
        });
    }

}