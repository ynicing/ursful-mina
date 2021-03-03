package com.ursful.framework.mina.server.listener;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.common.support.IPAddress;

import java.util.List;

/**
 * 类名：IClientReady
 * 创建者：huangyonghua
 * 日期：2019/3/7 8:50
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IServerListener extends IOrder {
    void serverStarted(String name, List<String> ips, int port);
}
