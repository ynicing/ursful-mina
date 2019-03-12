package com.ursful.framework.mina.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.ursful.framework.mina.client.message.MessageReader;
import com.ursful.framework.mina.client.message.MessageSession;
import com.ursful.framework.mina.client.mina.ClientHandler;
import com.ursful.framework.mina.client.mina.coder.ClientCodecFactory;
import com.ursful.framework.mina.client.mina.handler.*;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.common.support.Session;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.server.cluster.handler.ClusterClientMessagesHandler;
import com.ursful.framework.mina.server.cluster.handler.ClusterClientPresenceHandler;
import com.ursful.framework.mina.server.cluster.handler.ClusterClientPresenceInfoHandler;
import com.ursful.framework.mina.server.cluster.handler.ClusterClientServerInfoHandler;
import com.ursful.framework.mina.server.cluster.listener.IClusterClientStatus;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UrsClient implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(UrsClient.class);

    private String clientId;
    private String serverId;
    private boolean isCluster = false;

    private boolean autoConnected = false;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    private MessageReader messageReader;

    public void setMetaData(Map<String, Object> metaData) {
        if(metaData != null) {
            this.metaData = metaData;
        }
    }

    public void send(Packet packet){
        if(session != null){
            session.write(packet);
        }
    }

    public void enableCluster(boolean flag){
        this.isCluster = flag;
        this.metaData.put("CLIENT_TYPE", "SERVER_CLIENT");
    }

    public boolean isCluster(){
        return isCluster;
    }

    public String getCid(){
        return clientId + "@" + serverId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }



    public UrsClient(){
        this.metaData.put("CLIENT_TYPE", "CLIENT");
    }

    private int port;
    private String host;

    private ClientHandler handler;

    public UrsClient(String cid, String host, int port){
        this();
        this.clientId = cid;
        this.host = host;
        this.port = port;
        this.addresses.add(new InetSocketAddress(host, port));
    }

    private List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

    public UrsClient(String cid, String ips){
        this();
        this.clientId = cid;
        if(ips != null) {
            String[] ip = ips.split(",");
            for(String ipStr : ip){
                String[] ipAndPort = ipStr.split(":");
                if(ipAndPort.length == 2) {
                    this.addresses.add(new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                }
            }
        }
    }

    private IoSession session;

    private NioSocketConnector connector;
    private int count = 0;


    @Override
    public void run() {
        try{
            autoConnected = true;

            connector = new NioSocketConnector();

            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientCodecFactory()));


            handler = new ClientHandler(this);
            handler.register(new ClientKeepAliveHandler());
            handler.register(new ClientInfoHandler());


            if(isCluster()){
                handler.register(new ClusterClientMessagesHandler());
                handler.register(new ClusterClientPresenceHandler());
                handler.register(new ClusterClientPresenceInfoHandler());
                handler.register(new ClusterClientServerInfoHandler());
            }else{
                ClientMessagesHandler messagesHandler = new ClientMessagesHandler();
                handler.register(messagesHandler);
                handler.register(new ClientPresenceHandler());
                handler.register(new ClientPresenceInfoHandler());
                handler.register(new ClientServerInfoHandler());
                messageReader = new MessageReader(messagesHandler);
                messageReader.startup();
            }

            connector.setHandler(handler);

            connector.setConnectTimeoutMillis(10*1000);

//            connector.setDefaultRemoteAddress(new InetSocketAddress(host, port));

            connector.addListener(new IoServiceListener() {
                @Override
                public void serviceActivated(IoService ioService) throws Exception {
                }
                @Override
                public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {
                }
                @Override
                public void serviceDeactivated(IoService ioService) throws Exception {
                    logger.error("serviceDeactivated", ioService.toString());
                }
                @Override
                public void sessionCreated(IoSession ioSession) throws Exception {

                }
                @Override
                public void sessionClosed(IoSession ioSession) throws Exception {
                    logger.error("sessionClosed");
                }
                @Override
                public void sessionDestroyed(IoSession arg0) throws Exception {
                    if(!isCluster()) {
                        List<IClientStatus> statuses = InterfaceManager.getObjects(IClientStatus.class);
                        for (IClientStatus status : statuses) {
                            ThreadUtils.start(new Runnable() {
                                @Override
                                public void run() {
                                    status.clientClose(getCid());
                                }
                            });
                        }
                        reconnect();
                    }else{
                        List<IClusterClientStatus> statuses = InterfaceManager.getObjects(IClusterClientStatus.class);
                        for (IClusterClientStatus status : statuses) {
                            ThreadUtils.start(new Runnable() {
                                @Override
                                public void run() {
                                    status.serverClientClose(getCid(), host, port);
                                }
                            });
                        }
                    }
                }
            });

            if(!isCluster()) {
                reconnect();
            }else{
                if(!connect(null)) {
                    List<IClusterClientStatus> statuses = InterfaceManager.getObjects(IClusterClientStatus.class);
                    for (IClusterClientStatus status : statuses) {
                        ThreadUtils.start(new Runnable() {
                            @Override
                            public void run() {
                                status.serverClientClose(getCid(), host, port);
                            }
                        });
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void reconnect(){
        if(!autoConnected){
            return;
        }
        for (;;) {
            InetSocketAddress address = null;
            int length = addresses.size();
            if(length == 1){
                address = addresses.get(0);
            }else if(length > 1){
                int index =  count%length;
                if(index == 0){
                    count = 0;
                }
                address = addresses.get(index);
                count++;
            }else{
                logger.error("no host/port." + this.serverId);
                return;
            }
            if (connect(address)){
                break;
            }else{
                try {
                    Thread.sleep(3000);// 连接失败后,重连间隔5s
                } catch (InterruptedException e1) {
                }
            }
        }
    }

    private boolean connect(InetSocketAddress address) {
        try {
            if(address == null){
                address = new InetSocketAddress(host, port);
            }
            ConnectFuture future = connector.connect(address);
            future.awaitUninterruptibly(); // 等待连接创建成功
            session = future.getSession(); // 获取会话
            logger.info("[" + this.clientId + "] Successful. Free Mem:" + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
            return true;
        } catch (Exception e) {
            logger.warn("[" + this.clientId + "]Connecting...[" + address + "]");
        }
        return false;
    }


    public void close(){
        this.autoConnected = false;
        if(session != null) {
            session.closeOnFlush();
        }
        connector.dispose();
    }

    public MessageSession getMessageSession(){
        if(session != null && !session.isActive()){
            session.closeOnFlush();
        }
        return new MessageSession(handler.getWriter());
    }




}
