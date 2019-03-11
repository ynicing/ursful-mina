package com.ursful.framework.mina.server;

import com.ursful.framework.mina.client.UrsClient;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.common.tools.DateUtils;
import com.ursful.framework.mina.common.tools.NetworkUtils;
import com.ursful.framework.mina.server.cluster.listener.IClusterClientStatus;
import com.ursful.framework.mina.server.mina.ServerHandler;
import com.ursful.framework.mina.server.mina.coder.CodecFactory;
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

    public List<InetSocketAddress> ipPortList = new ArrayList<InetSocketAddress>();
    private boolean enableTransfer;
    public void setClusterIps(String ips){
        ipPortList.clear();
        if(ips != null && !"".equals(ips)) {
            String[] ip = ips.split(",");
            for(String ipStr : ip){
                String[] ipAndPort = ipStr.split(":");
                if(ipAndPort.length == 2) {
                    ipPortList.add(new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                }
            }
        }
    }

    public void enableTransfer(boolean flag){
        this.enableTransfer = flag;
    }

    private int port;
    private String sid;
    private NioSocketAcceptor acceptor;

    public int getPort() {
        return port;
    }

    public UrsServer(String sid, int port) {
    	this.port = port;
        this.sid = sid;
    }

    private Map<String, UrsClient> ursClientMap = new HashMap<String, UrsClient>();

    @Override
    public void run() {

        if(enableTransfer) {
            InterfaceManager.register(new IClusterClientStatus() {
                @Override
                public void serverClientReady(String cid, String host, int port) {
                    List<String> ips = NetworkUtils.getHostAddress();
                    ips.add("127.0.0.1");
                    ips.add("localhost");
                    if(ips.contains(host) && getPort() == port){
                        return;
                    }
                    String key = host + ":" + port;
                    if(!ursClientMap.containsKey(key)){
                        logger.info(cid + "-----------serverClientReady. add new........" + key);
                        UrsClient c = new UrsClient(sid, host, port);
                        c.getMetaData().put("SERVER_PORT", getPort());
                        c.enableCluster(true);
                        c.run();
                        ursClientMap.put(key, c);
                    }else{
                        logger.info(cid + "-----------serverClientReady. not add........" + key);
                    }
                }

                @Override
                public void serverClientClose(String cid, String host, int port) {
                    String key = host + ":" + port;
                    UrsClient c = ursClientMap.get(key);
                    if(c != null) {
                        logger.info(cid + "-----------serverClientClose..remove......." + key);
                        c.close();
                        ursClientMap.remove(key);
                    }else{
                        logger.info(cid + "-----------serverClientClose..not exist......." + key);
                    }

                }
            });
        }

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        try {
            ServerHandler serverHandler = new ServerHandler(sid, PacketProcessor.getProcessor());
            acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors());
            acceptor.getSessionConfig().setTcpNoDelay(true);
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
            acceptor.setHandler(serverHandler);
            acceptor.bind(new InetSocketAddress(port));
            logger.info("端口 :" + port);

            if(enableTransfer){
                List<String> ip = NetworkUtils.getHostAddress();
                for(InetSocketAddress address : ipPortList){
                    if(("localhost".equals(address.getHostName())
                         || "127.0.0.1".equals(address.getHostName())
                         || ip.contains(address.getHostName()))
                            && this.port == address.getPort()){
                        continue;
                    }
                    UrsClient client = new UrsClient(this.sid, address.getHostName(), address.getPort());
                    client.enableCluster(true);
                    client.getMetaData().put("SERVER_PORT", this.port);
                    ursClientMap.put(address.getHostName() + ":" + address.getPort(), client);
                    client.run();
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



}