package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
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
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClientManager {

    private static Logger logger = LoggerFactory.getLogger(ClientManager.class);

    public static ClientInfo findClientInfo(String id){
        if(id == null){
            return null;
        }
        List<String> cids = new ArrayList<String>(localClients.keySet());
        for (String cid : cids){
            if(id.equalsIgnoreCase(User.getId(cid))){
                Client client = localClients.get(cid);
                return new ClientInfo(client.getUser().getCid(), true, client.getMetaData());
            }
        }
        List<Map<String, ClientInfo>> mapList = new ArrayList<Map<String, ClientInfo>>(serverClientInfo.values());
        for (Map<String, ClientInfo> map : mapList){
            List<String> temp = new ArrayList<String>(map.keySet());
            for (String cid : temp){
                if(id.equalsIgnoreCase(User.getId(cid))){
                    return map.get(cid);
                }
            }
        }
        return null;
    }

    //client_type  分为 server_client 和 client
    private static Map<String, Client> localClients = new HashMap<String, Client>();
    private static Map<String, Client> localServerClients = new HashMap<String, Client>();

    //server-name(domain)
    private static Map<String, Map<String, ClientInfo>> serverClientInfo = new HashMap<String, Map<String, ClientInfo>>();


    public static void removeClientsInfo(String domain){
        serverClientInfo.remove(domain);
    }

    public static void updateClientInfo(String domain, String cid, boolean online, Map<String, Object> data){
        Map<String, ClientInfo> map = serverClientInfo.get(domain);
        if(map == null){
            map = new HashMap<String, ClientInfo>();
        }
        if(online) {
            map.put(cid, new ClientInfo(cid, online, data));
        }else{
            map.remove(cid);
        }
        serverClientInfo.put(domain, map);
        System.out.println("now clients:" +  map);
    }

    public static List<ClientInfo> getAllClientsInfo(){
        List<ClientInfo> list = new ArrayList<ClientInfo>();
        List<Client> users = new ArrayList<Client>(localClients.values());
        for(Client client : users){
            list.add(new ClientInfo(client.getUser().getCid(), true, client.getMetaData()));
        }
        List<Map<String, ClientInfo>> temp = new ArrayList<Map<String, ClientInfo>>(serverClientInfo.values());
        for(Map<String, ClientInfo> clients : temp){
            list.addAll(clients.values());
        }
        return list;
    }

    public static List<ClientInfo> getClientsInfo(){
        List<ClientInfo> list = new ArrayList<ClientInfo>();
        List<Client> users = new ArrayList<Client>(localClients.values());
        for(Client client : users){
            list.add(new ClientInfo(client.getUser().getCid(), true, client.getMetaData()));
        }
        return list;
    }

    public static List<ClientInfo> getServerClientsInfo(){
        List<ClientInfo> list = new ArrayList<ClientInfo>();
        List<Map<String, ClientInfo>> users = new ArrayList<Map<String, ClientInfo>>(serverClientInfo.values());
        for(Map<String, ClientInfo> clients : users){
            list.addAll(clients.values());
        }
        return list;
    }

//    public static List<String> getClientIds(){
//        return new ArrayList<String>(users.keySet());
//    }
//    public static List<Client> getClients(){
//        return new ArrayList<Client>(users.values());
//    }
//    public static List<Client> getClients(String domain){
//        List<Client> clients = new ArrayList<Client>();
//        Set<String> cids = new HashSet<String>(users.keySet());
//        for(String cid : cids){
//            if(cid.endsWith("@" + domain)){
//                clients.add(users.get(cid));
//            }
//        }
//        return clients;
//    }
    public static Client getServerClient(String cid){
        return localServerClients.get(cid);
    }

    public static Client getClient(String cid){
        return localClients.get(cid);
    }

    public static void register(Client client){
        ClientUser user = client.getUser();
        if(user != null) {
            if(!client.isServer()) {
                logger.info("Register client: " + user.getCid() + ">" + client.getMetaData().toString());
                localClients.put(user.getCid(), client);//在 broadCast 之后， 含有时间。
            }else{
                logger.info("Register server-client: " + user.getCid() + ">" + client.getMetaData().toString());
                localServerClients.put(user.getCid(), client);//在 broadCast 之后， 含有时间。
            }
        }
    }

    public static void deregister(Client client){
        ClientUser user = client.getUser();
        if(user != null) {
            if(!client.isServer()) {
                logger.info("Deregister client: " + user.getCid() + ">" + client.getMetaData().toString());
                localClients.remove(user.getCid());//移除离线状态，可能会在其他服务器上登录。
            }else{
                logger.info("Deregister server-client: " + user.getCid() + ">" + client.getMetaData().toString());
                localServerClients.remove(user.getCid());//移除离线状态，可能会在其他服务器上登录。
            }
        }
    }


    public static List<Client> getClients(){
        return new ArrayList<Client>(localClients.values());
    }

    public static List<Client> getServerClients(){
        return new ArrayList<Client>(localServerClients.values());
    }


    public static void broadcastServerClients(Packet packet){
        Collection<Client> clients = getServerClients();
        for(Client client : clients){
            client.write(packet);
        }
    }

    public static void broadcastClients(Packet packet){
        Collection<Client> clients = getClients();
        for(Client client : clients){
            client.write(packet);
        }
    }
}
