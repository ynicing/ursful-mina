package com.ursful.framework.mina.message.server;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.server.client.Client;

/**
 * 类名：IServerMessage
 * 创建者：huangyonghua
 * 日期：2019/3/6 10:09
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IServerMessage  extends IOrder {
    boolean received(Message message, Client client);
}
