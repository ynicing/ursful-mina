package com.ursful.framework.mina.message;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IClientStatus;
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
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2021/2/25 14:46 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
 */
public class ServerClientTest {

    UrsServer server = null;

    boolean running = true;

    int count = 0;

    @Before
    public void before(){
        server = new UrsServer("server1", 19090);
        server.enableCluster();
        server.setClusterIps("127.0.0.1:19091");
        server.register(new MessagesHandler());
        server.register(new ClusterClientMessagesHandler());
        new Thread(server).start();
        System.out.println("start...");
    }

    @Test
    public void test() throws InterruptedException {
        System.out.println("go..");
        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());
                Map<String, Object> result = message.getData();
                Message reply = message.reply();
                reply.put("hi", "hello");
                reply.putAll(result);
                session.sendMessage(reply);
                count++;
            }
        });

        UrsManager.register(new IClientStatus() {
            @Override
            public void clientReady(UrsClient client, String cid) {
                MessageManager.getManager().setClient(client);
                Message message = new Message();
                message.setType(Message.CHAT);
                message.setFromCid(client.getCid());
                message.setToCid(client.getCid());
                message.put("x", "self test.");
                message.put("y",  123);
                Message reply = MessageManager.getManager().getReply(message, 1000);

                Assert.assertEquals("hello", reply.get("hi"));
                System.out.println("reply:....." + reply);

                running = false;

                count++;

            }

            @Override
            public void clientClose(UrsClient client, String cid) {

            }
        });

        UrsClient client = new UrsClient("client", "127.0.0.1", 19090);
        client.getMetaData().put("force", "true");
        client.register(new ClientMessagesHandler());
        new Thread(client).run();

        while (running){
            Thread.sleep(1000);
        }

        client.close();

        server.close();

        Assert.assertEquals(2, count);

    }
}
