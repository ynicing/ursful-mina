package com.ursful.framework.mina.test;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.server.MessagesHandler;
import com.ursful.framework.mina.server.UrsServer;
import com.ursful.framework.mina.server.client.*;
import com.ursful.framework.mina.server.client.Client;

/**
 * 类名：Server1
 * 创建者：huangyonghua
 * 日期：2019/2/25 14:08
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Server {
    public static void main(String[] args) throws Exception{
        UrsServer server = new UrsServer("server1", 19090);
        server.enableCluster();
        server.setClusterIps("127.0.0.1:19091");
        server.register(new MessagesHandler());
        server.register(new ClusterClientMessagesHandler());
        new Thread(server).start();
    }
}
