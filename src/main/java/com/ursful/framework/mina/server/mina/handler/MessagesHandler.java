package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.client.message.Message;
import com.ursful.framework.mina.client.message.MessageCreator;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.message.IServerMessage;
import com.ursful.framework.mina.server.mina.ClientInfo;
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
        String id = reader.readString();
        String fromCid = reader.readString();
        String toCid = reader.readString();
        int type = reader.readShort();
        Object data = null;
        if(reader.available() > 0){
            data = reader.readObject();
        }
        if(id.startsWith("reply-")){
            logger.info("Reply:" + id);
            Client userClient = ClientManager.getClient(toCid);
            if(userClient != null){//直接发给对方
                logger.info("Client exists:" + toCid);
                ByteWriter writer = new ByteWriter(reader.getBytes());
                userClient.write(writer.getPacket());
            }else{
                String server = User.getDomain(toCid);
                String sic = server + "@" + c.getUser().getDomain();
                Client serverClient = ClientManager.getClient(sic);
                if(serverClient != null){
                    logger.info("Resent : " + toCid + ">>>" + sic);
                    Packet packet = PacketCreator.getMessageTransfer(fromCid, toCid, reader.getBytes());
                    serverClient.write(packet);
                }else{
                    logger.info(toCid + " > Client not exist:" + sic);
                }

            }
            return;
        }

        if(type == 0 && toCid.equals("system@" + c.getUser().getDomain())){
            Message message = new Message();
            message.setId(id);
            message.setFromCid(fromCid);
            message.setToCid(toCid);
            message.setType(type);
            message.setData(data);

            logger.info("System message receive:" + toCid);
            //"system.servers"
            List<IServerMessage> messages = InterfaceManager.getObjects(IServerMessage.class);
            for(IServerMessage serverMessage : messages){
                serverMessage.received(message, c);
            }
            return;
        }
        if(fromCid.equals(toCid)){
            logger.info("Self message receive:" + toCid);
            ByteWriter writer = new ByteWriter(reader.getBytes());
            c.write(writer.getPacket());
            return;
        }
        if(toCid.startsWith("all@")){//全部接收者
//            String clientId = c.getSession().getAttribute(Client.CLIENT_ID_KEY).toString();
//            String localDomain = ClientUser.getId(clientId);//server1
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
            }
            return;
        }
        // c1@server1 c2@server1        c3@server2 c4@server2
        // c1@server1 to all@server2
        Client userClient = ClientManager.getClient(toCid);
        if(userClient != null){//直接发给对方
            logger.info("sent to client:" + toCid);
            ByteWriter writer = new ByteWriter(reader.getBytes());
            userClient.write(writer.getPacket());
        }else{
            //可能是集群
            Client server = ClientManager.getClient(ClientUser.getDomain(toCid) + "@" + c.getUser().getDomain());
            if(server != null) {
                logger.info("sent to server-client client:" + ClientUser.getDomain(toCid) + "@" + c.getUser().getDomain());
                Packet packet = PacketCreator.getMessageTransfer(fromCid, toCid, reader.getBytes());
                server.write(packet);
            }else{
                if(type == 0){//默认返回！！！
                    Message message = new Message();
                    message.setId("reply-" + id);
                    message.setFromCid("system@" + c.getUser().getDomain());
                    message.setToCid(fromCid);
                    c.write(message.getPacket());
                    logger.info("reply...:" + fromCid);
                }
                logger.warn("Unknown client:" + toCid);
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
        /*
        MessageHandler handler = processor.getHandler(type);
        if(handler != null){
            Collection<Client> clients = ClientManager.getAllClients();
            for(Client client : clients) {
                ByteReader rd = new ByteReader(reader.getBytes());
                rd.skip(2);
                rd.readString();
                rd.readShort();
                handler.handlePacket(rd, client);
            }
        }else{
            System.out.println("Message unhandle(all@)." + type);
        }*/
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}