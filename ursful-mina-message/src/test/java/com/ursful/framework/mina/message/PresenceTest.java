package com.ursful.framework.mina.message;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.message.client.ClientMessagesHandler;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.server.MessagesHandler;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;
import com.ursful.framework.mina.server.UrsServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * 类名：ServerClientTest
 * 创建者：huangyonghua
 * <p>创建时间:2021/2/25 14:46 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class PresenceTest {

    UrsServer server = null;

    boolean running = true;

    String result = "";

    @Before
    public void before(){
        server = new UrsServer("PresenceTest", 19090);
        server.enableCluster();
        server.register(new MessagesHandler());
        server.register(new ClusterClientMessagesHandler());
        new Thread(server).start();
    }

    private UrsClient client;

    @Test
    public void test() throws InterruptedException {

        UrsManager.register(new IClientStatus() {
            @Override
            public void clientReady(UrsClient client, String cid) {
                result += "open";
                ThreadUtils.start(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        client.close();
                    }
                });
            }

            @Override
            public void clientClose(UrsClient client, String cid) {
                result += "close";
                running = false;
            }
        });

        client = new UrsClient("client", "127.0.0.1", 19090);
        client.getMetaData().put("force", "true");
        client.register(new ClientMessagesHandler());
        new Thread(client).run();

        int time = 0;
        while (time < 60 && running){
            time++;
            Thread.sleep(1000);
        }

        server.close();

        Assert.assertEquals("openclose", result);

    }
}
