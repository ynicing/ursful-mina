package com.ursful.framework.mina.server.client;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javax.script.ScriptEngine;

import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.tools.AesOfb;
import org.apache.mina.core.session.IoSession;

import com.ursful.framework.mina.server.tools.PacketCreator;
import com.ursful.framework.mina.server.tools.TimerManager;

public class Client {
    public static final String CLIENT_ID_KEY = "CLIENT_ID";
    public static final String CLIENT_KEY = "CLIENT";
    private AesOfb send;
    private AesOfb receive;
    private IoSession session;
    private ClientUser user;
    private boolean loggedIn = false;
    private String accountName;
    private int world;
    private long lastPong;
    private boolean hasPacketLog = false;
    private Set<String> macs = new HashSet<String>();
    private Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
    private ScheduledFuture<?> idleTask = null;
    private int lastActionId = 0;
    public int packetnum = 0;

    private boolean isServer;

    public boolean isServer() {
        return isServer;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    private Map<String, Object> metaData;

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    private String parentId;// server id

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private String serverId;//cluser server id;

    public void write(Packet packet){
        if (session != null) {
            session.write(packet);
        }
//        ClientUser user = getUser();
//        Client c = RouteManager.getServer(user.getCid());
//        if(c != null){
//            c.write(packet);
//        }else {
//            if (session != null) {
//                session.write(packet);
//            }
//        }
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    private boolean useCryptor = true;

    public boolean isUseCryptor() {
        return useCryptor;
    }

    public void setUseCryptor(boolean useCryptor) {
        this.useCryptor = useCryptor;
    }

    public Client(IoSession session) {
        this.session = session;
    }

    public Client(AesOfb send, AesOfb receive, IoSession session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    public synchronized AesOfb getReceiveCrypto() {
        return receive;
    }

    public synchronized AesOfb getSendCrypto() {
        return send;
    }

    public synchronized IoSession getSession() {
        return session;
    }


    public ClientUser getUser() {
        return user;
    }

    public void setUser(ClientUser user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

 
    public int finishLogin(boolean success) {
        return 0;
    }

    public int login(String login, String pwd, boolean ipMacBanned) {
        return 1;
    }


 
    public void disconnect() { }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void pongReceived(long time) {
        lastPong = System.currentTimeMillis();
    }

    public void sendPing() {
        final long now = System.currentTimeMillis();
        lastPong = 0;
        getSession().write(PacketCreator.getPing(now));
        TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    if (lastPong - now >= 5000 || lastPong == 0) {
                        if (getSession().isConnected()) {
                            getSession().closeOnFlush();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }, 5000); // note: idletime gets added to this too
    }
 



    public Set<String> getMacs() {
        return Collections.unmodifiableSet(macs);
    }

    public void setScriptEngine(String name, ScriptEngine e) {
        engines.put(name, e);
    }

    public ScriptEngine getScriptEngine(String name) {
        return engines.get(name);
    }

    public void removeScriptEngine(String name) {
        engines.remove(name);
    }

    public ScheduledFuture<?> getIdleTask() {
        return idleTask;
    }

    public void setIdleTask(ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

     

     
    public boolean hasPacketLog() {
        return this.hasPacketLog;
    }
    FileOutputStream pL_fos = null;
    OutputStreamWriter pL_osw = null;


    public int getLastActionId() {
        return lastActionId;
    }

    public void setLastActionId(int actionId) {
        this.lastActionId = actionId;
    }
 
    public void changeChannel(int channel) { }

    @Override
    public String toString() {
        return "Client{" +
                "user=" + user +
                ", isServer=" + isServer +
                '}';
    }
}