package com.ursful.framework.mina.server.mina.support;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.server.client.Client;

/**
 * 类名：IClientReady
 * 创建者：huangyonghua
 * 日期：2019/3/7 8:50
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IServerClientStatus  extends IOrder {
    void serverClientConnect(Client client);
    void serverClientClose(Client client);
}
