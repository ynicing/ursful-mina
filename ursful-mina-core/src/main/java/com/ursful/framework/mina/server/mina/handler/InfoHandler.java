package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.tools.*;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.mina.support.IServerClientStatus;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class InfoHandler extends AbstractInfoHandler {

    public void handleInfo(String clientId, boolean isServer, Map<String, Object> metaData, Client c) {
//        String id = User.getId(clientId);
//        if("all".equals(id)
//                || "system".equals(id)){
//            ByteWriter writer = new ByteWriter();
//            writer.writeShort(Opcode.INFO.ordinal());
//            writer.writeString("系统关键字，禁止使用");
//            c.write(writer.getPacket());
//            c.getSession().closeOnFlush();
//            //c.write();
//            return;
//        }
        Object force = metaData.get("force");
        if(force == null || !"true".equalsIgnoreCase(force.toString())) {
            Collection<Client> clients = ClientManager.getClients();
            for (Client client : clients) {
                if (clientId.equalsIgnoreCase(client.getUser().getCid())) {
                    ByteWriter writer = new ByteWriter();
                    writer.writeShort(Opcode.INFO.ordinal());
                    writer.writeString("[" + clientId + "]已被其他用户使用。");
                    c.write(writer.getPacket());
                    c.getSession().closeOnFlush();
                    return;
                }
            }
        }

        ClientUser user = new ClientUser();
        user.setClient(c);
        user.setCid(clientId);// 必须是 id@domain/resource
        c.setUser(user);
        c.setMetaData(metaData);//先设置metaData 才能知道是否server
        if(c.isServer()){
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IServerClientStatus> statuses = UrsManager.getObjects(IServerClientStatus.class);
                    for (IServerClientStatus status : statuses) {
                        status.serverClientConnect(c);
                    }
                }
            });
        }

        c.getSession().setAttribute(Client.CLIENT_ID_KEY, user.getCid());

        List<IClientManager> list = UrsManager.getObjects(IClientManager.class);
        for(IClientManager manager : list){
            manager.register(c);
        }
    }

}