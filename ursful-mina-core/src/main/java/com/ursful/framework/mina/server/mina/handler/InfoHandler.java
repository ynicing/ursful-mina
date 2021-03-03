package com.ursful.framework.mina.server.mina.handler;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.*;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.handle.IInfoHandle;
import com.ursful.framework.mina.server.mina.support.IServerClientStatus;

import java.util.List;


public class InfoHandler extends AbstractInfoHandler {

    public void handleInfo(Client client, ClientInfo info){


        List<IInfoHandle> handles = UrsManager.getObjects(IInfoHandle.class);
        for (IInfoHandle handle : handles) {
            if (!handle.preHandle(client, info)){
                return;
            }
        }

        ClientUser user = new ClientUser();
        user.setClient(client);
        user.setCid(info.getCid());// 必须是 id@domain/resource
        client.setUser(user);
        client.setServer(info.isServer());
        client.setMetaData(info.getData());//先设置metaData 才能知道是否server
        client.getSession().setAttribute(Client.CLIENT_ID_KEY, user.getCid());

        if(client.isServer()){
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IServerClientStatus> statuses = UrsManager.getObjects(IServerClientStatus.class);
                    for (IServerClientStatus status : statuses) {
                        status.serverClientConnect(client);
                    }
                }
            });
        }

        for (IInfoHandle handle : handles) {
            handle.handle(client, info);
        }

    }

}