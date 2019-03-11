package com.ursful.framework.mina.server;


import com.ursful.framework.mina.client.message.Message;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.message.IServerMessage;
import com.ursful.framework.mina.server.mina.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：Server1
 * 创建者：huangyonghua
 * 日期：2019/2/25 14:08
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Server1 {



    public static void main(String[] args) throws Exception{
        InterfaceManager.register(new IServerMessage() {
            @Override
            public void received(Message message, Client client) {
                if ("servers".equals(message.getData())) {
                    List<ClientInfo> infos = ClientManager.getClientServerInfos();
                    List<String> temp = new ArrayList<String>();
                    for (ClientInfo info : infos) {
                        temp.add(info.getCid() + "[" + info.getData().toString() + "]");
                    }
                    Message reply = message.reply(temp);
                    client.write(reply.getPacket());
                }
            }
        });

        UrsServer server = new UrsServer("server1", 9090);
//        server.setClusterIps("127.0.0.1:9091,127.0.0.1:9093");
//        server.setClusterIps("127.0.0.1:9090,127.0.0.1:9091,127.0.0.1:9092");
        server.enableTransfer(true);
        new Thread(server).start();
    }
}
