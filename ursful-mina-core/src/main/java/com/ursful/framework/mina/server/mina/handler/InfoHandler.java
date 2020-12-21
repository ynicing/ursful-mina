package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.*;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class InfoHandler implements PacketHandler {

    @Override
    public int opcode() {
        return Opcode.INFO.ordinal();
    }

    @Override
    public void handlePacket(ByteReader reader, Client c) {
        String serverClientId = reader.readString();
        int isServer = reader.readByte();
        Map<String, Object> metaData = reader.readObject();
        String id = User.getId(serverClientId);
        if("all".equals(id)
                || "system".equals(id)){
            ByteWriter writer = new ByteWriter();
            writer.writeShort(Opcode.INFO.ordinal());
            writer.writeString("系统关键字，禁止使用");
            c.write(writer.getPacket());
            c.getSession().closeOnFlush();
            //c.write();
            return;
        }
        String force = (String)metaData.get("force");
        Collection<Client> clients = ClientManager.getAllClients();
        if(!"true".equalsIgnoreCase(force)) {
            for (Client client : clients) {
                if (serverClientId.equalsIgnoreCase(client.getUser().getCid())) {
                    ByteWriter writer = new ByteWriter();
                    writer.writeShort(Opcode.INFO.ordinal());
                    writer.writeString("[" + serverClientId + "]已被其他用户使用。");
                    c.write(writer.getPacket());
                    c.getSession().closeOnFlush();
                    return;
                }
            }
        }

        ClientUser user = new ClientUser();
        user.setClient(c);
        user.setCid(serverClientId);// 必须是 id@domain/resource
        c.setUser(user);
        c.setIsServer(isServer == 1);
        if(c.isServer()){
            String host = (String)c.getSession().getAttribute("client_ip");
            if("localhost".equals(host) || "127.0.0.1".equals(host)){
                List<String> ips = NetworkUtils.getHostAddress();
                if(!ips.contains(host)) {
                    ips.add(host);
                }
                metaData.put("server_host", StringUtils.join(ips));
            }else{
                metaData.put("server_host", host);
            }

            if(metaData.containsKey("server_port")) {//自定义不含server port
                int port = (int) metaData.get("server_port");
                List<IClusterClientStatus> statuses = UrsManager.getObjects(IClusterClientStatus.class);
                for (IClusterClientStatus status : statuses) {
                    ThreadUtils.start(new Runnable() {
                        @Override
                        public void run() {
                            status.serverClientReady(c.getUser().getCid(), host, port);
                        }
                    });
                }
            }
        }
        c.setMetaData(metaData);
        c.getSession().setAttribute(Client.CLIENT_ID_KEY, user.getCid());

        ClientManager.register(c);
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}