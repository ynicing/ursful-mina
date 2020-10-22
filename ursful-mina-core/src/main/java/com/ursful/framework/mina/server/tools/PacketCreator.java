package com.ursful.framework.mina.server.tools;


import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientInfo;

import java.util.*;

public class PacketCreator {

    public static Packet getHello(short version, String sid) {
        return getHello(version, sid, null, null);
    }

    public static Packet getHello(short version, String sid, byte[] sendIv, byte[] recvIv) {
        ByteWriter writer = new ByteWriter();
        writer.writeByte(0xff);
        writer.writeShort(Opcode.HELLO.ordinal());
        writer.writeShort(version);
        writer.writeString(sid);
        if(sendIv != null) {
            writer.writeBytes(sendIv);
        }
        if(recvIv != null) {
            writer.writeBytes(recvIv);
        }
        return writer.getPacket();
    }
 
    public static Packet getPing(long time) {
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PING.ordinal());
        //long time = System.currentTimeMillis();
        writer.writeLong(time);
        return writer.getPacket();
    }

    public static Packet getPresence(ClientInfo info) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PRESENCE.ordinal());
        writer.writeString(info.getCid());
        writer.writeByte(info.getOnline()?1:0);
        info.getData().put("ONLINE", info.getOnline());
        writer.writeObject(info.getData());
        return writer.getPacket();
    }

    public static Packet getServerInfo(Collection<ClientInfo> info) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.SERVER_INFO.ordinal());
        for(ClientInfo key : info) {
            writer.writeString(key.getCid());
            key.getData().put("ONLINE", true);
            writer.writeObject(key.getData());
        }
        return writer.getPacket();
    }

    public static Packet getPresenceInfo(Collection<ClientInfo> info, boolean online) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PRESENCE_INFO.ordinal());
        for(ClientInfo key : info) {
            writer.writeString(key.getCid());
            key.getData().put("ONLINE", online);
            writer.writeObject(key.getData());
        }
        return writer.getPacket();
    }

    public static Packet getMessageTransfer(String type, String fromCid, String toCid, byte [] data) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.MESSAGE.ordinal());
        writer.writeString(type);
        writer.writeString(fromCid);
        writer.writeString(toCid);
        writer.writeBytes(data);
        return writer.getPacket();
    }

//    public static Packet getTransfer(String fromCid, String toCid, byte [] data) {//String cid, int ops, int type,
//        ByteWriter writer = new ByteWriter();
//        writer.writeShort(Opcode.TRANSFER.ordinal());
//        writer.writeString(fromCid);
//        writer.writeString(toCid);
//        writer.writeBytes(data);
//        return writer.getPacket();
//    }



}