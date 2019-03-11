package com.ursful.framework.mina.client.tools;


import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteWriter;

import java.util.Map;
import java.util.Random;

public class ClientPacketCreator {

    public static Packet getInfo(Opcode ops, String sid, String cid, boolean isServer, Map<String, Object> info) {
        ByteWriter writer = new ByteWriter();
        writer.writeShort(ops.ordinal());
        writer.writeString(cid + "@" + sid);
        writer.writeByte(isServer?1:0);
        writer.writeObject(info);
        return writer.getPacket();
    }

    public static Packet getServerInfo(){
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.SERVER_INFO.ordinal());
        return writer.getPacket();
    }

    public static Packet getPing(){
        long time = System.currentTimeMillis();
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PING.ordinal());
        writer.writeLong(time);
        return writer.getPacket();
    }

}