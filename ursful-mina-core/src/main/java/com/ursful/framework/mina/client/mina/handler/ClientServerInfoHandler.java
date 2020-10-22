package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;

import java.util.*;

public class ClientServerInfoHandler implements ClientPacketHandler {

    public int opcode() {
        return Opcode.SERVER_INFO.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        while (reader.available() > 0){
            String cid = reader.readString();
            Map<String, Object> data = reader.readObject();
            data.put("ONLINE", true);
            map.put(cid, data);
        }
        List<IServerInfo> infos = UrsManager.getObjects(IServerInfo.class);
        for(IServerInfo info : infos){
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    info.info(map);
                }
            });
        }
    }


}