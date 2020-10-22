package com.ursful.framework.mina.test.clientSend2ClusterClient;

import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.message.cluster.ClusterClientMessagesHandler;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ynice on 2020/10/12.
 */
public class ExtClusterClientMessagesHandler extends ClusterClientMessagesHandler{

    private static Logger logger = LoggerFactory.getLogger(ExtClusterClientMessagesHandler.class);

    @Override
    public void handlePacket(ByteReader reader, PacketWriter writer) {
        String type = reader.readString();
        String fromCid = reader.readString();
        String toCid = reader.readString();
        byte[] data = reader.readBytes();// Message内容
        if(Message.BROADCAST.equals(type)){//send to everybody.
            ClientManager.broadcastWithoutServer(new ByteArrayPacket(data));
            return;
        }
        Client client = ClientManager.getClient(toCid);
        if(client != null){
            client.write(new ByteArrayPacket(data));
        }
        //所有消息保存数据库
        Message msg = Message.parseMessage(data);
        logger.info("Save Type:" + type +  ", no client : " + toCid + " from: " + fromCid + ">" + msg.getData());

    }
}
