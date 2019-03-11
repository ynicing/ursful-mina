package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 类名：ClientManager
 * 创建者：huangyonghua
 * 日期：2019/2/27 14:47
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClientManager {

    private static Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private static Map<String, Client> users = new HashMap<String, Client>();

    public static List<ClientInfo> getClientInfos(){
        List<ClientInfo> list = new ArrayList<ClientInfo>();
        for(Client client : users.values()){
            if(client.getMetaData() == null ||  "CLIENT".equals(client.getMetaData().get("CLIENT_TYPE"))) {
                list.add(new ClientInfo(client.getUser().getCid(), true, client.getMetaData()));
            }
        }
        return list;
    }

    public static List<ClientInfo> getClientServerInfos(){
        List<ClientInfo> list = new ArrayList<ClientInfo>();
        for(Client client : users.values()){
            if(client.getMetaData() != null &&  "SERVER_CLIENT".equals(client.getMetaData().get("CLIENT_TYPE"))) {
                list.add(new ClientInfo(client.getUser().getCid(), true, client.getMetaData()));
            }
        }
        return list;
    }

    public static List<String> getClientIds(){
        return new ArrayList<String>(users.keySet());
    }
    public static List<Client> getClients(){
        return new ArrayList<Client>(users.values());
    }
    public static List<Client> getClients(String domain){
        List<Client> clients = new ArrayList<Client>();
        Set<String> cids = new HashSet<String>(users.keySet());
        for(String cid : cids){
            if(cid.endsWith("@" + domain)){
                clients.add(users.get(cid));
            }
        }
        return clients;
    }


    public static Client getClient(String cid){
        return users.get(cid);
    }

    public static Collection<Client> getAllClients(){
        return new ArrayList<Client>(users.values());
    }

    public static void register(Client client){
        ClientUser user = client.getUser();
        if(user != null) {
            logger.info("Register : " + user.getCid() + ">" + client.getMetaData().toString());
            if("CLIENT".equals(client.getMetaData().get("CLIENT_TYPE"))) {
                List<ClientInfo> us = getClientInfos();
                us.addAll(OtherServerClientManager.getAllClientInfos());
                Packet status = PacketCreator.getPresenceInfo(us, true);
                client.write(status);//客户端发送当前所有客户端在线状态
                users.put(user.getCid(), client);//在 broadCast 之后， 含有时间。
                Packet packet = PacketCreator.getPresence(new ClientInfo(user.getCid(), true, client.getMetaData()));
                broadcast(packet); //转发 告诉其他客户端我上线了
            }else{
                List<ClientInfo> us = getClientInfos();
                Packet status = PacketCreator.getPresenceInfo(us, true);
                client.write(status);//客户端发送当前所有客户端在线状态

                users.put(user.getCid(), client);//在 broadCast 之后， 含有时间。
                List<ClientInfo> list = getClientServerInfos();
                Packet packet = PacketCreator.getServerInfo(list);
                broadcastServer(packet);
            }
        }
    }

    public static void deregister(Client client){
        ClientUser user = client.getUser();
        if(user != null) {
            if("CLIENT".equals(client.getMetaData().get("CLIENT_TYPE"))) {
                Packet packet = PacketCreator.getPresence(new ClientInfo(user.getCid(), false, client.getMetaData()));
                broadcast(packet);
            }else{
                List<ClientInfo> clients = OtherServerClientManager.removeClientInfo(client.getUser().getId());
                Packet packet = PacketCreator.getPresenceInfo(clients, false);
                broadcastWithoutServer(packet);
            }
            logger.info("Deregister : " + user.getCid() + ">" + client.getMetaData().toString());
            users.remove(user.getCid());//移除离线状态，可能会在其他服务器上登录。
        }
    }

    public static void broadcast(Packet packet){
        Collection<Client> clients = getAllClients();
        for(Client client : clients){
            client.write(packet);
        }
    }

    public static void broadcastServer(Packet packet){
        Collection<Client> clients = getAllClients();
        for(Client client : clients){
            if(client.isServer()){
                client.write(packet);
            }
        }
    }

    public static void broadcastWithoutServer(Packet packet){
        Collection<Client> clients = getAllClients();
        for(Client client : clients){
            if(!client.isServer()){
                client.write(packet);
            }
        }
    }
}
