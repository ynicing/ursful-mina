package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TestHandler implements PacketHandler {

    private static Logger logger = LoggerFactory.getLogger(TestHandler.class);

    public static Map<String, String> keys = new HashMap<String, String>();

    @Override
    public int opcode() {
        return Opcode.TEST.ordinal();
    }

    @Override
    public void handlePacket(ByteReader reader, Client c) {
        String name = c.getSession().getRemoteAddress().toString();
        logger.info(name + " size : " + keys.size());

    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}