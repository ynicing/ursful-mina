package com.ursful.framework.mina.common.packet;

public interface Packet extends java.io.Serializable {

    public byte[] getBytes();

    public Runnable getOnSend();

    public void setOnSend(Runnable onSend);
}