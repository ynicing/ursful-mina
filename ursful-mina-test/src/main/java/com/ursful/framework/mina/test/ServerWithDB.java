package com.ursful.framework.mina.test;

import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.server.MessagesHandler;
import com.ursful.framework.mina.server.UrsServer;

/**
 * Created by ynice on 2020/10/14.
 */
public class ServerWithDB {
    public static void main(String[] args) throws Exception{
        UrsServer server = new UrsServer("server1", 9090);
        server.enableCluster();
        server.register(new ExtMessagesHandler());
        server.register(new ClusterClientMessagesHandler());
        new Thread(server).start();
    }
}
