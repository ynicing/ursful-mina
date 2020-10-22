package com.ursful.framework.mina.server;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.IPAddress;
import com.ursful.framework.mina.common.tools.DateUtils;
import com.ursful.framework.mina.common.tools.NetworkUtils;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
import com.ursful.framework.mina.server.mina.ServerHandler;
import com.ursful.framework.mina.server.mina.coder.CodecFactory;
import com.ursful.framework.mina.server.mina.packet.PacketHandler;
import com.ursful.framework.mina.server.mina.packet.PacketProcessor;
import com.ursful.framework.mina.server.tools.TimerManager;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void enableCluster(){
        this.enableTransfer = true;
    }

    private int port;
    private String sid;
    private NioSocketAcceptor acceptor;
    private ServerHandler serverHandler;
    private PacketProcessor processor;

    public int getPort() {
        return port;
    }

    private UrsServer(){}

    public UrsServer(String sid, int port) {
    	this.port = port;
        this.sid = sid;
        this.processor = PacketProcessor.getProcessor();
        this.serverHandler = new ServerHandler(this.sid, this.processor);
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

    @Override
    public void run() {

        if(enableTransfer) {
            UrsManager.register(new IClusterClientStatus() {
                @Override
                public void serverClientReady(String cid, String host, int port) {
                    List<String> ips = NetworkUtils.getHostAddress();
                    ips.add("127.0.0.1");
                    ips.add("localhost");
                    if (ips.contains(host) && getPort() == port) {
                        return;
                    }
                    String key = host + ":" + port;
                    if (!ursClientMap.containsKey(key)) {
                        logger.info(cid + "-----------serverClientReady. add new........" + key);
                        createClient(host, port);
                    } else {
                        logger.info(cid + "-----------serverClientReady. existed........" + key);
                    }
                }

                @Override
                public void serverClientClose(String cid, String host, int port) {
                    String key = host + ":" + port;
                    UrsClient c = ursClientMap.get(key);
                    if (c != null) {
                        logger.info(cid + "-----------serverClientClose..remove......." + key);
                        c.close();
                        ursClientMap.remove(key);
                    } else {
                        logger.info(cid + "-----------serverClientClose..not exist......." + key);
                    }

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
        manager.register(new Runnable() {
            @Override
            public void run() {
                System.gc();
                logger.info("System.gc");
            }
        }, 24*3600*1000, DateUtils.getTimeMillisToNextDate());//每天一次垃圾回收。
    }


    private void createClient(String host, int port){
        UrsClient client = new UrsClient(this.sid, host, port);
        client.enableCluster();
        for(ClientPacketHandler packetHandler : clientPacketHandlers){
            client.register(packetHandler);
        }
        client.getMetaData().put("SERVER_PORT", this.port);
        ursClientMap.put(host + ":" + port, client);
        client.run();
    }

}