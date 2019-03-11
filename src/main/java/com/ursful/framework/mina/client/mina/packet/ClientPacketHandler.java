package com.ursful.framework.mina.client.mina.packet;

import com.ursful.framework.mina.common.tools.ByteReader;
import org.apache.mina.core.session.IoSession;


public interface ClientPacketHandler {
    int opcode();
    void handlePacket(ByteReader reader, IoSession session);
}