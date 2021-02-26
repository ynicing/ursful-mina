package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;

/**
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2021/2/24 14:19 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
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
