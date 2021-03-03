package com.ursful.framework.mina.server.tools;


import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteWriter;

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

    public static Packet getPresence(ClientInfo info, boolean isTransfer) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PRESENCE.ordinal());
        writer.writeByte(isTransfer?1:0);
        writer.writeString(info.getCid());
        writer.writeByte(info.getOnline()?1:0);
        writer.writeByte(info.isChanged()?1:0);
        writer.writeObject(info.getData());
        return writer.getPacket();
    }

    public static Packet getPresence(ClientInfo info, Map<String, Object> changeData, boolean isTransfer) {
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PRESENCE.ordinal());
        writer.writeByte(isTransfer?1:0);
        writer.writeString(info.getCid());
        writer.writeByte(info.getOnline()?1:0);
        writer.writeByte(1);//changed
        Map<String, Object> data = new HashMap<String, Object>(info.getData());
        data.putAll(changeData);
        writer.writeObject(data);
        return writer.getPacket();
    }

    public static Packet getPresenceInfo(Collection<ClientInfo> info, boolean isTransfer) {//String cid, int ops, int type,
        ByteWriter writer = new ByteWriter();
        writer.writeShort(Opcode.PRESENCE_INFO.ordinal());
        writer.writeByte(isTransfer?1:0);
        for(ClientInfo key : info) {
            writer.writeString(key.getCid());
            writer.writeByte(key.getOnline()?1:0);//只有 online 或者 offline
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