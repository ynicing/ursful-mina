package com.ursful.framework.mina.message.server;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageCreator;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class MessagesHandler implements PacketHandler {

    private static Logger logger = LoggerFactory.getLogger(MessagesHandler.class);

    @Override
    public int opcode() {
        return Opcode.MESSAGE.ordinal();
    }

    @Override
    public void handlePacket(ByteReader reader, Client c) {

        //格式：[to] [type] [data]
        // 2bytes Message.
        Message message = Message.parseMessage(reader);
//        String id = reader.readString();
//        String type = reader.readString();
//        String fromCid = reader.readString();
//        String toCid = reader.readString();
//        Object data = null;
//        if(reader.available() > 0){
//            data = reader.readObject();
//        }
        if(message.getId().startsWith("reply-")){
            logger.info("Reply:" + message.getId());
            Client userClient = ClientManager.getClient(message.getToCid());
            if(userClient != null){//直接发给对方
                logger.info("Client exists:" + message.getToCid());
                ByteWriter writer = new ByteWriter(reader.getBytes());
                userClient.write(writer.getPacket());
            }else{
                String server = User.getDomain(message.getToCid());
                String sic = server + "@" + c.getUser().getDomain();
                Client serverClient = ClientManager.getClient(sic);
                if(serverClient != null){
                    logger.info("Resent : " + message.getToCid() + ">>>" + sic);
                    Packet packet = PacketCreator.getMessageTransfer(
                            message.getType(), message.getFromCid(), message.getToCid(), reader.getBytes());
                    serverClient.write(packet);
                }else{
                    logger.info(message.getToCid() + " > Client not exist:" + sic);
                }
            }
            return;
        }

        if(Message.BROADCAST.equalsIgnoreCase(message.getType())){//全部接收者
            sendLocalServer(reader.getBytes());//本地服务
            //转发到其他服务。
            Collection<Client> clients = ClientManager.getAllClients();
            logger.info("sent to server-client clients:" + clients);
            for(Client client : clients){
                if(client.isServer()) {
                    //message.setToCid("all@" + client.getUser().getId());
                    Packet packet = PacketCreator.getMessageTransfer(
                            message.getType(), message.getFromCid(), message.getToCid(), reader.getBytes());
                    client.write(packet);
                }
            }
            /*
            if(toCid.endsWith("@" + c.getUser().getDomain())){//本服务接收者
                logger.info("sent to local clients:" + c.getUser().getDomain());
                sendLocalServer(reader.getBytes());
            }else{
                String domain = toCid.substring("all@".length());
                if("all".equals(domain)){//其他所有服务，包括本地服务。
                    logger.info("sent to local clients2:" + c.getUser().getDomain());
                    sendLocalServer(reader.getBytes());//本地服务
                    //转发到其他服务。
                    Message message = new Message();
                    message.setId(id);
                    message.setFromCid(fromCid);
                    message.setToCid(toCid);
                    message.setType(type);
                    message.setData(data);
                    Collection<Client> clients = ClientManager.getAllClients();
                    logger.info("sent to server-client clients:" + clients);
                    for(Client client : clients){
                        if(client.isServer()) {
                            //message.setToCid("all@" + client.getUser().getId());
                            Packet msg  = MessageCreator.createMessage(message);
                            Packet packet = PacketCreator.getMessageTransfer(fromCid, message.getToCid(), msg.getBytes());
                            client.write(packet);
                        }
                    }
                }else{

                    //指定服务。
                    Client routeClient = ClientManager.getClient(domain + "@" + c.getUser().getDomain());
                    if(routeClient != null){
                        logger.info("sent to server-client client:" + domain + "@" + c.getUser().getDomain());
                        Packet packet = PacketCreator.getMessageTransfer(fromCid, "all@" + domain, reader.getBytes());
                        routeClient.write(packet);
                    }else{
                        logger.warn("Unknown server:" + toCid);
                    }
                }
            }*/
        }else{
            if(message.getFromCid().equals(message.getToCid())){
                logger.info("Self message receive:" + message.getToCid());
                ByteWriter writer = new ByteWriter(reader.getBytes());
                c.write(writer.getPacket());
                return;
            }
            // c1@server1 c2@server1        c3@server2 c4@server2
            // c1@server1 to all@server2
            Client userClient = ClientManager.getClient(message.getToCid());
            if(userClient != null){//直接发给对方
                logger.info("sent to client:" + message.getToCid());
                ByteWriter writer = new ByteWriter(reader.getBytes());
                userClient.write(writer.getPacket());
            }else{
                //可能是集群
                Client server = ClientManager.getClient(message.getToUser().getDomain() + "@" + c.getUser().getDomain());
                if(server != null) {
                    logger.info("sent to server-client client:" + message.getToUser().getDomain() + "@" + c.getUser().getDomain());
                    Packet packet = PacketCreator.getMessageTransfer(
                            message.getType(), message.getFromCid(), message.getToCid(), reader.getBytes());
                    server.write(packet);
                }else{
                    logger.warn("Unknown client:" + message.getToCid());
                }
            }
        }

    }

    private void sendLocalServer(byte[] data) {
        Collection<Client> clients = ClientManager.getAllClients();
        Packet packet = new ByteArrayPacket(data);
        for(Client client : clients) {
            if(!client.isServer()) {
                client.write(packet);
            }
        }
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}