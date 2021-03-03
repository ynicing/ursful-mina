package com.ursful.framework.mina.server.client.listener;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.ClientUser;
import com.ursful.framework.mina.server.mina.ClientManager;
import com.ursful.framework.mina.server.tools.PacketCreator;

/**
 * 类名：IClientCloseListener
 * 创建者：huangyonghua
 * <p>创建时间:2021/3/2 10:47 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IClientCloseListener extends IOrder {
    void close(Client client);
}
