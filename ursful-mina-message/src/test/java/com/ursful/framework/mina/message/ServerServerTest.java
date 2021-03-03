package com.ursful.framework.mina.message;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.server.MessagesHandler;
import com.ursful.framework.mina.server.UrsServer;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.listener.IServerListener;
import com.ursful.framework.mina.server.mina.support.IServerClientStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2021/3/3 9:10 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
 */
public class ServerServerTest {

    private UrsServer server1 = null;
    private  UrsServer server2 = null;

    private int done = 0;
    private int clients = 0;

    @Test
    public void test() throws InterruptedException {


        UrsManager.register(new IServerListener() {
            @Override
            public void serverStarted(String name, List<String> ips, int port) {
               if(port != 19093){
                   done = 1;
               }else{
                   server2 = new UrsServer("server3", 19094);
                   server2.enableCluster();
                   server2.setClusterIps("127.0.0.1:19093");
                   server2.register(new MessagesHandler());
                   server2.register(new ClusterClientMessagesHandler());
                   new Thread(server2).start();
               }
            }
        });

        UrsManager.register(new IServerClientStatus() {
            @Override
            public void serverClientConnect(String server, Client client) {
                System.out.println(client.getUser().getCid() + ">" + client.getMetaData());
                clients++;
            }

            @Override
            public void serverClientClose(String server, Client client) {

            }
        });

        server1 = new UrsServer("server2", 19093);
        server1.enableCluster();
        server1.setClusterIps("127.0.0.1:19091");
        server1.register(new MessagesHandler());
        server1.register(new ClusterClientMessagesHandler());
        new Thread(server1).start();

        int time = 0;
        while (time < 60 && (done == 0 || done < 3)){
            if(done > 0){
                done++;
            }
            time++;
            Thread.sleep(1000);
        }


        server1.close();
        server2.close();

//        Assert.assertEquals(clients, 2);
    }

}
