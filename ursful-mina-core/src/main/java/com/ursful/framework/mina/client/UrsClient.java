package com.ursful.framework.mina.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ursful.framework.mina.client.mina.ClientHandler;
import com.ursful.framework.mina.client.mina.coder.ClientCodecFactory;
import com.ursful.framework.mina.client.mina.handler.*;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.IClientStatus;
import com.ursful.framework.mina.common.support.IPAddress;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.common.cluster.handler.ClusterClientPresenceHandler;
import com.ursful.framework.mina.common.cluster.handler.ClusterClientPresenceInfoHandler;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
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
    private String resource;
    private boolean isCluster = false;

    private boolean autoConnected = false;

    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    private IPAddress currentAddress;
    private List<IPAddress> addresses = new ArrayList<IPAddress>();

    public void setMetaData(Map<String, Object> metaData) {
        if(metaData != null) {
            this.metaData = metaData;
        }
    }

    public void send(Packet packet){
        if(session != null && !session.isActive()){
            session.closeOnFlush();
        }
        if(session != null){
            session.write(packet);
        }
    }

    public void enableCluster(){
        this.isCluster = true;
        this.metaData.put("client_type", "SERVER_CLIENT");

        this.clientHandler.register(new ClusterClientPresenceHandler());
        this.clientHandler.register(new ClusterClientPresenceInfoHandler());
    }

    public boolean isCluster(){
        return isCluster;
    }

    public String getCid(){
        String cs = clientId + "@" + serverId;
        if(resource != null && resource.length() > 0){
            cs += "/" + resource;
        }
        return cs;
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



    private UrsClient(){
    }

    protected ClientHandler clientHandler;

    public ClientHandler getClientHandler(){
        return this.clientHandler;
    }

    public UrsClient(String cid, String host, int port){
        this(cid, null, host, port);
    }

    private void init(){
        this.clientHandler = new ClientHandler(this);
        this.clientHandler.register(new ClientPresenceHandler());
        this.clientHandler.register(new ClientPresenceInfoHandler());
    }

    public String getResource(){
        return this.resource;
    }

    public UrsClient(String cid, String resource, String host, int port){
        this.clientId = cid;
        this.resource = resource;
        this.metaData.put("client_type", "CLIENT");
        this.currentAddress = new IPAddress(host, port);
        this.addresses.add(this.currentAddress);
        init();
    }

    public UrsClient(String cid, String resource, String ips){
        this.clientId = cid;
        this.resource = resource;
        this.metaData.put("client_type", "CLIENT");
        addIps(ips);
        if(this.addresses.isEmpty()){
            throw new RuntimeException("No address found.");
        }
        this.currentAddress = this.addresses.get(0);
        init();
    }

    public UrsClient(String cid, String ips){
        this(cid, null, ips);
    }


    public void addIps(String ips){
        if(ips != null) {
            String[] ip = ips.split(",");
            for (String ipStr : ip) {
                String[] ipAndPort = ipStr.split(":");
                if (ipAndPort.length == 2) {
                    this.addresses.add(new IPAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                }
            }
        }
    }

    protected IoSession session;

    private NioSocketConnector connector;
    private int count = 0;


    public void register(ClientPacketHandler handler){
        this.clientHandler.register(handler);
    }

//    public void register(IClientStartup startup){
//        if(!startups.contains(startup)){
//            startups.add(startup);
//        }
//    }

    @Override
    public void run() {
        try{
            autoConnected = true;

            connector = new NioSocketConnector();

            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientCodecFactory()));


            clientHandler.register(new ClientKeepAliveHandler());
            clientHandler.register(new ClientInfoHandler());



            connector.setHandler(clientHandler);

            connector.setConnectTimeoutMillis(10*1000);

//            for (IClientStartup startup : startups){
//                startup.startup(this);
//            }

//            connector.setDefaultRemoteAddress(new InetSocketAddress(host, port));

            UrsClient thisClient = this;

            connector.addListener(new IoServiceListener() {
                @Override
                public void serviceActivated(IoService ioService) throws Exception {
                }
                @Override
                public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {
                }
                @Override
                public void serviceDeactivated(IoService ioService) throws Exception {
                }
                @Override
                public void sessionCreated(IoSession ioSession) throws Exception {
                }
                @Override
                public void sessionClosed(IoSession ioSession) throws Exception {
                }
                @Override
                public void sessionDestroyed(IoSession arg0) throws Exception {
                    clientClose(thisClient);
                }
            });

            if(!connect(this.currentAddress)) {
                clientClose(thisClient);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clientClose(UrsClient thisClient){
        if(!isCluster()) {
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IClientStatus> statuses = UrsManager.getObjects(IClientStatus.class);
                    for (IClientStatus status : statuses) {
                        status.clientClose(thisClient, getCid());
                    }
                }
            });
            reconnect();
        }else{
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IClusterClientStatus> statuses = UrsManager.getObjects(IClusterClientStatus.class);
                    for (IClusterClientStatus status : statuses) {
                        status.serverClientClose(getCid(), currentAddress.getIp(), currentAddress.getPort());
                    }
                }
            });
        }
    }

    public void clientReady(){
        UrsClient thisClient = this;
        if(!isCluster()) {
            //等待 ClientHandler中的clientReady
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IClientStatus> statuses = UrsManager.getObjects(IClientStatus.class);
                    for (IClientStatus status : statuses) {
                        status.clientReady(thisClient, getCid());
                        status.clientReady(getCid());
                    }
                }
            });
        }else{
            ThreadUtils.start(new Runnable() {
                @Override
                public void run() {
                    List<IClusterClientStatus> statuses = UrsManager.getObjects(IClusterClientStatus.class);
                    for (IClusterClientStatus status : statuses) {
                        status.serverClientReady(getCid(), currentAddress.getIp(), currentAddress.getPort());
                    }
                }
            });
        }
    }

    private void reconnect(){
        if(!autoConnected){
            return;
        }
        for (;;) {
            IPAddress address = null;
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

    private boolean connect(IPAddress address) {
        try {
            this.currentAddress = address;
            ConnectFuture future = connector.connect(new InetSocketAddress(address.getIp(), address.getPort()));
            future.awaitUninterruptibly(); // 等待连接创建成功
            session = future.getSession(); // 获取会话
            logger.info(this.clientId + " Successful. Free Mem:" + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
            return true;
        } catch (Exception e) {
            logger.warn(this.clientId + " Connecting...[" + address + "]");
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


}
