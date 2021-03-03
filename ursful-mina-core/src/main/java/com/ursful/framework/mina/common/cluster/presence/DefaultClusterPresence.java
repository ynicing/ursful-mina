package com.ursful.framework.mina.common.cluster.presence;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 类名：IPresence
 * 创建者：huangyonghua
 * 日期：2021/3/2 9:01
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultClusterPresence implements IClusterPresence {

    private static Logger logger = LoggerFactory.getLogger(DefaultClusterPresence.class);

    @Override
    public void presence(ClientInfo info) {
        if(!info.isServer()) {
            Packet packet = PacketCreator.getPresence(info, true);
            ClientManager.broadcastClients(packet);//转发，本地所有客户端。
            logger.info("Broadcast client online : " + info.getOnline());
            ClientManager.updateClientInfo(info);
        } else {
            //server-client 忽略
            logger.info("Server client online : " + info.getOnline());
        }
    }

    @Override
    public void metaDataChange(ClientInfo info) {
        if(!info.isServer()) {
            Packet packet = PacketCreator.getPresence(info, true);
            ClientManager.broadcastClients(packet);//转发，本地所有客户端。
            logger.info("Broadcast client online : " + info.getOnline());
            ClientManager.updateClientInfo(info);
        }
    }
}
