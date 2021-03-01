package com.ursful.framework.mina.server.client;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.server.mina.ClientInfo;

/**
 * 类名：IClientManager
 * 创建者：huangyonghua
 * <p>创建时间:2021/1/25 14:36 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IClientManager  extends IOrder {
    void register(Client client);
    void deregister(Client client);
    default void registerServerClient(ClientInfo info){};
}
