package com.ursful.framework.mina.common.packet;

public class ByteArrayPacket implements Packet {

    public static final long serialVersionUID = -1L;
    private byte[] data;
    private Runnable onSend;

    public ByteArrayPacket(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

    public Runnable getOnSend() {
        return onSend;
    }

    public void setOnSend(Runnable onSend) {
        this.onSend = onSend;
    }
}
