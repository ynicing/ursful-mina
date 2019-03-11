package com.ursful.framework.mina.server.message;

import com.ursful.framework.mina.client.message.Message;
import com.ursful.framework.mina.server.client.Client;

/**
 * 类名：IServerMessage
 * 创建者：huangyonghua
 * 日期：2019/3/6 10:09
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IServerMessage {
    void received(Message message, Client client);
}
