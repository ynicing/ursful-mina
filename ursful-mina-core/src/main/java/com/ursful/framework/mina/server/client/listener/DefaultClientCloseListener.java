package com.ursful.framework.mina.server.client.listener;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;

/**
 * 类名：DefaultClientCloseListener
 * 创建者：huangyonghua
 * <p>创建时间:2021/3/2 10:47 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultClientCloseListener implements IClientCloseListener {
    public void close(Client client){
        ClientManager.deregister(client);
        ClientUser user = client.getUser();
        if(user != null) {
            if(!client.isServer()) {
                Packet packet = PacketCreator.getPresence(new ClientInfo(user.getCid(), false, client.getMetaData()), false);
                ClientManager.broadcastClients(packet);//本地服务端的客户端
                ClientManager.broadcastServerClients(packet);//其他服务端的客户端
            }else{
                ClientManager.removeClientsInfo(user.getDomain());
                //server-client断开，需要将本地的server-client对应的clients状态修改为离线
            }
        }
    }
}
