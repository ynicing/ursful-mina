package com.ursful.framework.mina.common.cluster.handler;

import com.ursful.framework.mina.client.mina.packet.ClientPacketHandler;
import com.ursful.framework.mina.client.mina.packet.PacketWriter;
import com.ursful.framework.mina.common.Opcode;
import com.ursful.framework.mina.common.UrsManager;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.ByteReader;
import com.ursful.framework.mina.common.tools.ThreadUtils;
import com.ursful.framework.mina.common.cluster.listener.IClusterClientStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterClientServerInfoHandler implements ClientPacketHandler {

    private static Logger logger = LoggerFactory.getLogger(ClusterClientServerInfoHandler.class);

    public int opcode() {
        return Opcode.SERVER_INFO.ordinal();
    }

    public void handlePacket(ByteReader reader, PacketWriter writer) {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        while (reader.available() > 0){
            String cid = reader.readString();
            Map<String, Object> data = reader.readObject();
            data.put("ONLINE", true);
            map.put(cid, data);
            String host = (String)data.get("SERVER_HOST");
            if(data.containsKey("SERVER_PORT")) {
                int port = (int) data.get("SERVER_PORT");
                List<IClusterClientStatus> statuses = UrsManager.getObjects(IClusterClientStatus.class);
                for (IClusterClientStatus status : statuses) {
                    String server = "system@" + User.getDomain(writer.getSession().getAttribute("CLIENT_ID").toString());
                    String[] hosts = host.split(",");
                    for (String h : hosts) {
                        ThreadUtils.start(new Runnable() {
                            @Override
                            public void run() {
                                status.serverClientReady(server, h, port);
                            }
                        });
                    }
                }
            }
        }

        logger.info("servers clients : " + map);
    }


}