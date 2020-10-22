package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;
import java.util.Map;


public class ServerInfoHandler implements PacketHandler {

    @Override
    public int opcode() {
        return Opcode.SERVER_INFO.ordinal();
    }

    @Override
    public void handlePacket(ByteReader reader, Client c) {
        List<ClientInfo> infos = ClientManager.getClientServerInfos();
        Packet packet = PacketCreator.getServerInfo(infos);
        c.write(packet);
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}