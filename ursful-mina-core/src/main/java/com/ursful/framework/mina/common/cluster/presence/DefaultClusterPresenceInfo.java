package com.ursful.framework.mina.common.cluster.presence;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

import java.util.List;

/**
 * 类名：IPresence
 * 创建者：huangyonghua
 * 日期：2021/3/2 9:01
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class DefaultClusterPresenceInfo implements IClusterPresenceInfo {
    @Override
    public void presences(List<ClientInfo> clientInfos) {
        for (ClientInfo clientInfo : clientInfos) {
            ClientManager.updateClientInfo(clientInfo);
        }
        Packet packet = PacketCreator.getPresenceInfo(clientInfos);
        ClientManager.broadcastClients(packet);//转发，本地所有客户端。
    }
}
