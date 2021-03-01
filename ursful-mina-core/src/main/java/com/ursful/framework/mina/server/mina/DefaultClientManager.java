package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;

/**
 * 类名：DefaultClientManager
 * 创建者：huangyonghua
 * <p>创建时间:2021/2/24 14:19 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultClientManager implements IClientManager {
    @Override
    public void register(Client client) {
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

    @Override
    public void deregister(Client client) {
        ClientManager.deregister(client);
        ClientUser user = client.getUser();
        if(user != null) {
            if(!client.isServer()) {
                Packet packet = PacketCreator.getPresence(new ClientInfo(user.getCid(), false, client.getMetaData()));
                ClientManager.broadcastClients(packet);//本地服务端的客户端
                ClientManager.broadcastServerClients(packet);//其他服务端的客户端
            }else{
                ClientManager.removeClientsInfo(user.getDomain());
                //server-client断开，需要将本地的server-client对应的clients状态修改为离线
            }
        }
    }
}
