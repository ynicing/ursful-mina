package com.ursful.framework.mina.server.client.listener;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;

/**
 * 类名：DefaultClientInfoListener
 * 创建者：huangyonghua
 * <p>创建时间:2021/3/2 10:47 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultClientInfoListener implements IClientInfoListener {
    public void open(Client client, ClientInfo info){
        ClientManager.register(client);
        ClientUser user = client.getUser();
        if(user != null) {
            if (!client.isServer()) {
                List<ClientInfo> us = ClientManager.getAllClientsInfo();
                Packet status = PacketCreator.getPresenceInfo(us);
                client.write(status);//客户端发送当前所有客户端在线状态

                Packet packet = PacketCreator.getPresence(new ClientInfo(user.getCid(), true, client.getMetaData()));
                ClientManager.broadcastClients(packet);//本地服务端的客户端
                ClientManager.broadcastServerClients(packet);//其他服务端的客户端
            }else{
                List<ClientInfo> us = ClientManager.getClientsInfo();
                Packet status = PacketCreator.getPresenceInfo(us);
                client.write(status);//客户端发送当前所有客户端在线状态
            }
        }
    }
}
