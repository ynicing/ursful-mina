package com.ursful.framework.mina.server.mina.handle;

import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.listener.IClientInfoListener;
import com.ursful.framework.mina.server.mina.ClientManager;

import java.util.Collection;
import java.util.List;

/**
 * 类名：DefaultInfoHandle
 * 创建者：huangyonghua
 * 日期：2021/3/2 09:55
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultInfoHandle implements IInfoHandle{
    @Override
    public boolean preHandle(Client client, ClientInfo clientInfo) {
        Object force = clientInfo.getData().get("force");
        if(force == null || !"true".equalsIgnoreCase(force.toString())) {
            List<Client> clients = null;
            if(client.isServer()){
                clients = ClientManager.getServerClients();
            }else{
                clients = ClientManager.getClients();
            }
            for (Client c : clients) {
                if (clientInfo.getCid().equalsIgnoreCase(c.getUser().getCid())) {
                    ByteWriter writer = new ByteWriter();
                    writer.writeShort(Opcode.INFO.ordinal());
                    writer.writeShort(1);//error code
                    writer.writeString("[" + clientInfo.getCid() + "]已被其他用户使用。");
                    c.write(writer.getPacket());
                    c.getSession().closeOnFlush();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void handle(Client client, ClientInfo clientInfo) {

        List<IClientInfoListener> list = UrsManager.getObjects(IClientInfoListener.class);
        for(IClientInfoListener listener : list){
            listener.open(client, clientInfo);
        }

    }
}
