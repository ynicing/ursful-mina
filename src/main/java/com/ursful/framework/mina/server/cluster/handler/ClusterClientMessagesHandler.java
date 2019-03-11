package com.ursful.framework.mina.server.cluster.handler;

import com.ursful.framework.mina.client.message.*;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ByteWriter;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.ClientManager;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class ClusterClientMessagesHandler implements ClientPacketHandler {

    private static Logger logger = LoggerFactory.getLogger(ClusterClientMessagesHandler.class);

    public int opcode() {
        return Opcode.MESSAGE.ordinal();
    }

    public void handlePacket(ByteReader reader, IoSession session) {
        String fromCid = reader.readString();
        String toCid = reader.readString();
        byte[] data = reader.readBytes();
        if(toCid.startsWith("all@")){//send to everybody.
            ClientManager.broadcastWithoutServer(new ByteArrayPacket(data));
            return;
        }
        Client client = ClientManager.getClient(toCid);
        if(client != null){
            client.write(new ByteArrayPacket(data));
        }else{
            replyServer(data, session);
            logger.warn("Unknown cluster server:" + toCid);
        }
    }

    private void replyServer(byte[] message, IoSession session){
        ByteReader reader = new ByteReader(message);
        reader.skip(2);
        String id = reader.readString();
        String fromCid = reader.readString();
        String toCid = reader.readString();
        int type = reader.readShort();
        Object data = null;
        int available = reader.available();
        if(available > 0){
            data = reader.readObject();
        }
        logger.info("Cluster message>>>>" + toCid);
//        if(id.startsWith("reply-")){
//            Client client = ClientManager.getClient(toCid);
//            if(client == null){
//                logger.info("Message dead >>>>" + toCid);
//            }else{
//                logger.info("Message sent to dist >>>>" + toCid);
//                client.write(new ByteArrayPacket(reader.getBytes()));
//            }
//            return;
//        }
        if(type == 0 && !toCid.startsWith("all@")){
            Message msg = new Message();
            msg.setId("reply-" + id);
            msg.setToCid(fromCid);
            msg.setFromCid("system@" + User.getDomain(toCid));
            msg.setData(data);
            session.write(msg.getPacket());
        }else{
            logger.info("do.nothing..." + toCid);
        }
    }

//    static public Packet getReply(Connection connection, Packet packet, long timeout)
//            throws XMPPException
//    {
//        PacketFilter responseFilter = new PacketIDFilter(packet.getPacketID());
//        PacketCollector response = connection.createPacketCollector(responseFilter);
//
//        connection.sendPacket(packet);
//
//        // Wait up to a certain number of seconds for a reply.
//        Packet result = response.nextResult(timeout);
//
//        // Stop queuing results
//        response.cancel();
//
//        if (result == null) {
//            throw new XMPPException("No response from server.");
//        }
//        else if (result.getError() != null) {
//            throw new XMPPException(result.getError());
//        }
//        return result;
//    }


}