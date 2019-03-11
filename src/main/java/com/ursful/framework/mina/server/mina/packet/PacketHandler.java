package com.ursful.framework.mina.server.mina.packet;

import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.server.client.Client;


public interface PacketHandler {

    int opcode();

    void handlePacket(ByteReader reader, Client c);

    boolean validateState(Client c);
}