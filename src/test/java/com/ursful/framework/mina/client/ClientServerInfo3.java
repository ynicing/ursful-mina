package com.ursful.framework.mina.client;

import com.ursful.framework.mina.client.message.IServerInfo;
import com.ursful.framework.mina.client.tools.ClientPacketCreator;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.packet.Packet;

import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClientServerInfo3 {
    public static void main(String[] args) throws Exception{

        InterfaceManager.register(new IServerInfo() {
            @Override
            public void info(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        UrsClient client = new UrsClient("client", "127.0.0.1", 9092);
        new Thread(client).run();
        Thread.sleep(3*1000);

        Packet packet = ClientPacketCreator.getServerInfo();
        client.send(packet);

    }
}
