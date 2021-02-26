package com.ursful.framework.mina.test.clientSend2ClusterClient;


import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.message.IPresence;
import com.ursful.framework.mina.client.message.IPresenceInfo;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.message.MessageManager;
import com.ursful.framework.mina.message.client.IMessage;
import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;

import java.util.Map;

/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class ClusterClient {
    public static void main(String[] args) throws Exception{

        MessageManager.registerMessage(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
            System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getType() + ">" + message.getData());
            }
        });

        UrsManager.register(new IPresence() {
            @Override
            public void presence(String cid, boolean online, Map<String, Object> data) {
                System.out.println("user[" + cid + "]:" + (online ? "Online" : "Offline") + " >>> " + data);
            }
        });

        UrsManager.register(new IPresenceInfo() {
            @Override
            public void presences(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        UrsManager.register(new IClientStatus() {

            @Override
            public void clientReady(String cid) {
                System.out.println("clientReady:" + cid);
            }

            @Override
            public void clientClose(String cid) {
                System.out.println("clientClose:" + cid);
            }
        });

        UrsClient client = new UrsClient("websocket", "127.0.0.1", 19090);
        client.enableCluster();
        client.register(new ClusterClientMessagesHandler());

        new Thread(client).run();


    }
}
