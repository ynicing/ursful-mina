package com.ursful.framework.mina.client.mina.handler;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.common.tools.ByteReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ClientInfoHandler implements ClientPacketHandler {

    private Logger logger = LoggerFactory.getLogger(ClientInfoHandler.class);

    private UrsClient client;

    public ClientInfoHandler(UrsClient client){
        this.client = client;
    }

    public int opcode() {
        return Opcode.INFO.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        if(reader.available() > 0){
            int errorCode = reader.readShort();
            String reason = reader.readString();
            logger.error("error:" + errorCode + ", reason :" + reason);
            List<IClientStatus> statuses = UrsManager.getObjects(IClientStatus.class);
            for (IClientStatus status : statuses) {
                status.clientError(this.client, errorCode, reason);
            }
        }
    }
}