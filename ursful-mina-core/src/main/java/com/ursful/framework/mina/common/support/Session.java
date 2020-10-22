package com.ursful.framework.mina.common.support;

import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.packet.Packet;
import org.apache.mina.core.session.IoSession;

/**
 * 类名：Session
 * 创建者：huangyonghua
 * 日期：2019/3/1 15:48
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Session {

    private PacketWriter writer;

    public PacketWriter getWriter() {
        return writer;
    }

    public void setWriter(PacketWriter writer) {
        this.writer = writer;
    }

    public Session(){}

    public Session(PacketWriter writer){
        this.writer = writer;
    }

    public void send(Packet packet){
        if(this.writer != null){
            this.writer.sendPacket(packet);
        }
    }
}
