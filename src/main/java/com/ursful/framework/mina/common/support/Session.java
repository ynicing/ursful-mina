package com.ursful.framework.mina.common.support;

import com.ursful.framework.mina.common.packet.Packet;
import org.apache.mina.core.session.IoSession;

/**
 * 类名：Session
 * 创建者：huangyonghua
 * 日期：2019/3/1 15:48
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Session {

    private IoSession session;

    public Session(){}

    public Session(IoSession session){
        this.session = session;
    }

    public void send(Packet packet){
        if(this.session != null){
            this.session.write(packet);
        }
    }
}
