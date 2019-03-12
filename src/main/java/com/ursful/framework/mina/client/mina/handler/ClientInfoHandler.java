package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.exception.RefuseException;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientInfoHandler implements ClientPacketHandler {

    private Logger logger = LoggerFactory.getLogger(ClientInfoHandler.class);

    public int opcode() {
        return Opcode.INFO.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        if(reader.available() > 0){
            String reason = reader.readString();
            logger.error(reason);
            throw new RefuseException(reason);
        }
    }


}