package com.ursful.framework.mina.server.mina.packet;

import com.ursful.framework.mina.server.mina.handler.*;

import java.util.HashMap;
import java.util.Map;


public final class PacketProcessor {

    private static PacketProcessor processor = new PacketProcessor();
    private Map<Integer, PacketHandler> processorHandlers = new HashMap<Integer, PacketHandler>();

    private PacketProcessor() {
    }

    public PacketHandler getHandler(int packetId) {
        return processorHandlers.get(packetId);
    }

    public  void register(PacketHandler handler) {
        processorHandlers.put(handler.opcode(), handler);
    }

    public synchronized static PacketProcessor getProcessor() {
        processor.register(new TestHandler());
        processor.register(new KeepAliveHandler());
        processor.register(new InfoHandler());
        return processor;
    }

}