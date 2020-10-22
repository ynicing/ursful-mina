package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.client.tools.ClientPacketCreator;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteReader;
import org.apache.mina.core.session.IoSession;

public class ClientKeepAliveHandler implements ClientPacketHandler {

    public int opcode() {
        return Opcode.PING.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        Packet packet = ClientPacketCreator.getPing();
        writer.sendPacket(packet);
    }
}