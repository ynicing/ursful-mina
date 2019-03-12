package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.message.IPresenceInfo;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPresenceInfoHandler implements ClientPacketHandler{

    public int opcode() {
        return Opcode.PRESENCE_INFO.ordinal();
    }


    public void handlePacket(ByteReader reader, PacketWriter writer) {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        while (reader.available() > 0){
            String cid = reader.readString();
            Map<String, Object> data = reader.readObject();
            map.put(cid, data);
        }
        List<IPresenceInfo> presenceInfos = InterfaceManager.getObjects(IPresenceInfo.class);
        for(IPresenceInfo info : presenceInfos){
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    info.presences(map);
                }
            });
        }
    }

}