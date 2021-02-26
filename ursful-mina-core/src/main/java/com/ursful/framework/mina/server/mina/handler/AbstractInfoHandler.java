package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.*;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;

import java.util.Collection;
import java.util.List;
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
        handleInfo(clientId, isServer == 1, metaData, c);
    }

    public abstract void  handleInfo(String cid, boolean isServer, Map<String, Object> data, Client client);

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}