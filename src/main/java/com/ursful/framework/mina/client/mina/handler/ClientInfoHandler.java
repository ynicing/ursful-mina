package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.exception.RefuseException;
import com.ursful.framework.mina.client.message.IServerInfo;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientInfoHandler implements ClientPacketHandler {

    private Logger logger = LoggerFactory.getLogger(ClientInfoHandler.class);

    public int opcode() {
        return Opcode.INFO.ordinal();
    }

    public void handlePacket(ByteReader reader, IoSession session) {
        if(reader.available() > 0){
            String reason = reader.readString();
            logger.error(reason);
            throw new RefuseException(reason);
        }
    }


}