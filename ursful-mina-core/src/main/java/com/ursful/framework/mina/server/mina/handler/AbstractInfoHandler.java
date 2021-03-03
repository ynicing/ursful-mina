package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.*;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;

import java.util.Map;

/**
 * @author huangyonghua, jlis@qq.com
 */
public abstract class AbstractInfoHandler implements PacketHandler {

    @Override
    public int opcode() {
        return Opcode.INFO.ordinal();
    }

    @Override
    public void handlePacket(ByteReader reader, Client c) {
        String clientId = reader.readString();
        int isServer = reader.readByte();
        Map<String, Object> metaData = reader.readObject();
        handleInfo(c, new ClientInfo(clientId, isServer == 1, metaData));
    }

    public abstract void  handleInfo(Client client, ClientInfo info);

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}