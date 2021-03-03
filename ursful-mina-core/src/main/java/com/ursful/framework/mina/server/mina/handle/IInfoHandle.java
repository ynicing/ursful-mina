package com.ursful.framework.mina.server.mina.handle;

import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.server.client.Client;

/**
 * 类名：IInfoHandle
 * 创建者：huangyonghua
 * 日期：2021/3/2 09:55
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IInfoHandle {
    boolean preHandle(Client client, ClientInfo clientInfo);
    void handle(Client client, ClientInfo clientInfo);
}
