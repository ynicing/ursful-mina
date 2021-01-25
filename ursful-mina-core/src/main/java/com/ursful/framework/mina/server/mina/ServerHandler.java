package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.AesOfb;
import com.ursful.framework.mina.common.tools.BitTools;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.server.client.IClientManager;
import com.ursful.framework.mina.server.mina.coder.PacketDecoder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import com.ursful.framework.mina.server.mina.packet.PacketProcessor;
import com.ursful.framework.mina.server.tools.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;


public class ServerHandler extends IoHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private final static short VERSION = 1;
    private PacketProcessor processor;
    private String sid;


    public ServerHandler(String sid, PacketProcessor processor) {
        this.processor = processor;
        this.sid = sid;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        /*Runnable r = ((Packet) message).getOnSend();
        if (r != null) {
            r.run();
        }*/
        //System.out.println(message);
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        //MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        logger.error("Exception : " + cause.getMessage(), cause);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {

    	String address = session.getRemoteAddress().toString();

        String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
        session.setAttribute("client_ip", clientIP);

        byte ivSend[] = {82, 48, 120,  (byte) (Math.random() * 255)};
        byte ivRecv[] = {70, 114, 122, (byte) (Math.random() * 255)};

//        byte ivRecv[] = {1,2,3,4};
//        byte ivSend[] = {4,3,2,1};

        AesOfb sendCypher = new AesOfb(AesOfb.AES_KEY, ivSend);
        AesOfb recvCypher = new AesOfb(AesOfb.AES_KEY, ivRecv);

        Client client = new Client(sendCypher, recvCypher, session);
        client.setServerId(this.sid);

        Packet hello = PacketCreator.getHello(VERSION, sid, ivSend, ivRecv);
        session.write(hello);//刚开始不加密！！！

        session.setAttribute(Client.CLIENT_KEY, client);

        logger.info("Session Opened RecvIP: "+ address + " : " + client);

        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 6);

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        synchronized (session) {
            Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
            if (client != null) {
                List<IClientManager> list = UrsManager.getObjects(IClientManager.class);
                for(IClientManager manager : list){
                    manager.deregister(client);
                }
                ClientManager.deregister(client);
                client.disconnect();
                session.removeAttribute(Client.CLIENT_KEY);
                session.removeAttribute(Client.CLIENT_ID_KEY);
                if(client.getUser() != null) {
                    logger.warn("close : " + client.getUser().getCid());
                }else{
                    logger.warn("close 1 : " + client.getServerId());
                }
            }else {
                logger.warn("close 0: " + client);
            }
        }
        super.sessionClosed(session);
    }
    

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] content = (byte[]) message;
        ByteReader reader = new ByteReader(content);
        int packetId = reader.readShort();
        Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
        PacketHandler packetHandler = processor.getHandler(packetId);
        if (packetHandler != null && packetHandler.validateState(client)) {
            try {
                packetHandler.handlePacket(reader, client);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }else{
        	logger.warn("unhanlder message : " + packetId + ">" + reader);
        }
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
//        if (client != null && client.getUser() != null) {
//            System.out.println("ClientUser " + client.getUser().getCid() + " went idle!");
//        }
        if (client != null) {
            client.sendPing();
        }
        super.sessionIdle(session, status);
    }
}