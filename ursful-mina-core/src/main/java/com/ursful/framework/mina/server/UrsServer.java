package com.ursful.framework.mina.server;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.cluster.presence.DefaultClusterPresence;
import com.ursful.framework.mina.common.cluster.presence.DefaultClusterPresenceInfo;
import com.ursful.framework.mina.common.cluster.presence.IClusterPresence;
import com.ursful.framework.mina.common.cluster.presence.IClusterPresenceInfo;
import com.ursful.framework.mina.common.support.IPAddress;
import com.ursful.framework.mina.common.tools.DateUtils;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
import com.ursful.framework.mina.common.tools.NetworkUtils;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.client.listener.DefaultClientCloseListener;
import com.ursful.framework.mina.server.client.listener.DefaultClientInfoListener;
import com.ursful.framework.mina.server.client.listener.IClientCloseListener;
import com.ursful.framework.mina.server.client.listener.IClientInfoListener;
import com.ursful.framework.mina.server.listener.IServerListener;
import com.ursful.framework.mina.server.mina.ServerHandler;
import com.ursful.framework.mina.server.mina.coder.CodecFactory;
import com.ursful.framework.mina.server.mina.handle.DefaultInfoHandle;
import com.ursful.framework.mina.server.mina.handle.IInfoHandle;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import com.ursful.framework.mina.server.mina.packet.PacketProcessor;
import com.ursful.framework.mina.server.mina.support.IServerClientStatus;
import com.ursful.framework.mina.server.tools.TimerManager;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class UrsServer implements Runnable{

    public static long systemTimeDifference;

    private static Logger logger = LoggerFactory.getLogger(UrsServer.class);


    public List<IPAddress> ipPortList = new ArrayList<IPAddress>();
    private boolean enableTransfer = false;
    public void setClusterIps(String ips){
        ipPortList.clear();
        if(ips != null && !"".equals(ips)) {
            String[] ip = ips.split(",");
            for(String ipStr : ip){
                String[] ipAndPort = ipStr.split(":");
                if(ipAndPort.length == 2) {
                    ipPortList.add(new IPAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                }
            }
        }
    }

    private IClusterPresence clusterPresence = new DefaultClusterPresence();
    private IClusterPresenceInfo clusterPresenceInfo = new DefaultClusterPresenceInfo();

    public void removeClusterPresence() {
        UrsManager.deregister(clusterPresence);
    }

    public void removeClusterPresenceInfo() {
        UrsManager.deregister(clusterPresenceInfo);
    }

    public void enableCluster(){
        this.enableTransfer = true;
        UrsManager.register(clusterPresenceInfo);
        UrsManager.register(clusterPresence);
    }

    private int port;
    private String sid;
    private NioSocketAcceptor acceptor;
    private ServerHandler serverHandler;
    private PacketProcessor processor;

    private IClientCloseListener clientCloseListener = new DefaultClientCloseListener();
    private IClientInfoListener clientInfoListener = new DefaultClientInfoListener();

    private IInfoHandle infoHandle = new DefaultInfoHandle();

    public void removeDefaultInfoHandle(){
        UrsManager.deregister(infoHandle);
    }

    public int getPort() {
        return port;
    }

    private UrsServer(){}

    public void registerClientCloseListener(IClientCloseListener listener){
        UrsManager.deregister(clientCloseListener);
        clientCloseListener = listener;
        UrsManager.register(clientCloseListener);
    }

    public void registerClientInfoListener(IClientInfoListener listener){
        UrsManager.deregister(clientInfoListener);
        clientInfoListener = listener;
        UrsManager.register(clientInfoListener);
    }

    public UrsServer(String sid, int port) {
    	this.port = port;
        this.sid = sid;
        this.processor = PacketProcessor.getProcessor();
        this.serverHandler = new ServerHandler(this.sid, this.processor);
        registerClientCloseListener(clientCloseListener);
        registerClientInfoListener(clientInfoListener);
        UrsManager.register(infoHandle);
    }

    public void register(PacketHandler handler){
        this.processor.register(handler);
    }

    public void register(ClientPacketHandler handler){
        if(!this.clientPacketHandlers.contains(handler)) {
            this.clientPacketHandlers.add(handler);
        }
    }

    private List<ClientPacketHandler> clientPacketHandlers = new ArrayList<ClientPacketHandler>();


    private Map<String, UrsClient> ursClientMap = new HashMap<String, UrsClient>();

    private Runnable gcTask = new Runnable() {
        @Override
        public void run() {
            System.gc();
            logger.info("System.gc");
        }
    };

    @Override
    public void run() {

        if(enableTransfer) {
            final String serverId = this.sid;
            UrsManager.register(new IServerClientStatus() {
                @Override
                public void serverClientConnect(String server, Client client) {
                    if(serverId.equals(server)) {
                        if (client.getMetaData().containsKey("server_port")) {//自定义不含server port
                            String host = (String) client.getMetaData().get("server_host");
                            if (host == null) {
                                host = "127.0.0.1";
                            }
                            int port = (int) client.getMetaData().get("server_port");
                            String key = host + ":" + port;
                            if (!ursClientMap.containsKey(key)) {
                                createClient(host, port);
                            }
                        }
                    }
                }

                @Override
                public void serverClientClose(String server, Client client) {
                    if(serverId.equals(server)) {
                        String host = (String) client.getMetaData().get("server_host");
                        if (host == null) {
                            host = "127.0.0.1";
                        }
                        int port = (int) client.getMetaData().get("server_port");
                        String key = host + ":" + port;
                        UrsClient c = ursClientMap.get(key);
                        if (c != null) {
                            logger.info(key + "-----------serverClientClose..remove......." + key);
                            try {
                                c.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ursClientMap.remove(key);
                        }
                    }
                }
            });

            // 服务客户端
            UrsManager.register(new IClusterClientStatus() {
                @Override
                public void serverClientReady(String cid, String host, int port) {
//                    List<String> ips = NetworkUtils.getHostAddress();
//                    ips.add("127.0.0.1");
//                    ips.add("localhost");
//                    if (ips.contains(host) && getPort() == port) {
//                        return;
//                    }
//                    String key = host + ":" + port;
//                    if (!ursClientMap.containsKey(key)) {
//                        logger.info(cid + "-----------serverClientReady. add new........" + key);
//                        createClient(host, port);
//                    } else {
//                        logger.info(cid + "-----------serverClientReady. existed........" + key);
//                    }
                }

                @Override
                public void serverClientClose(String cid, String host, int port) {
                    String key = host + ":" + port;
                    UrsClient c = ursClientMap.get(key);
                    if (c != null) {
                        logger.info("remove server-client: " + cid + ">" + key);
                        c.close();
                        ursClientMap.remove(key);
                    } else {
                        logger.info("remove server-client note exist: " + cid + ">" + key);
                    }
                    //当server1 断开 server2时，将对应的用户信息全部标记为离线
                }
            });

        }

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        try {
            acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors());
            acceptor.getSessionConfig().setTcpNoDelay(true);
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
            acceptor.setHandler(serverHandler);
            acceptor.bind(new InetSocketAddress(port));
            logger.info("端口 :" + port);
            List<IServerListener> serverListeners = UrsManager.getObjects(IServerListener.class);
            for (IServerListener serverListener : serverListeners){
                serverListener.serverStarted(this.sid, NetworkUtils.getHostAddress(), port);
            }
            if(enableTransfer){
                //List<String> ip = NetworkUtils.getHostAddress();
                for(IPAddress address : ipPortList){
                    createClient(address.getIp(), address.getPort());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        TimerManager manager = TimerManager.getInstance();
        manager.register(gcTask, 24*3600*1000, DateUtils.getTimeMillisToNextDate());//每天一次垃圾回收。
    }


    public void close(){
        if(acceptor != null) {
            acceptor.dispose();
        }
        TimerManager manager = TimerManager.getInstance();
        manager.remove(gcTask);

        Set<UrsClient> clients = new HashSet<UrsClient>(ursClientMap.values());
        for (UrsClient client : clients){
            try {
                client.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        ursClientMap.clear();
    }

    private void createClient(String host, int port){
        UrsClient client = new UrsClient(this.sid, host, port);
        client.enableCluster();
        for(ClientPacketHandler packetHandler : clientPacketHandlers){
            client.register(packetHandler);
        }
        client.getMetaData().put("server_port", this.port);
        ursClientMap.put(host + ":" + port, client);
        client.run();

        logger.info("create server-client:" + host + ":" + port);
    }

}