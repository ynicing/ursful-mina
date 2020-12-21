package com.ursful.framework.mina.server.mina;

import com.ursful.framework.mina.server.client.Client;

import java.util.*;

/**
 * 类名：OtherServerClientManager
 * 创建者：huangyonghua
 * 日期：2019/3/5 16:51
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class OtherServerClientManager {
    private static Map<String, ClientInfo> clientInfoMap = new HashMap<String, ClientInfo>();

    public static List<ClientInfo> removeClientInfo(String serverId){
        List<ClientInfo> infos = new ArrayList<ClientInfo>();
        Set<String> cids = new HashSet<String>(clientInfoMap.keySet());
        for(String cid : cids){
            if(cid.endsWith("@" + serverId)){
                infos.add(clientInfoMap.get(cid));
                clientInfoMap.remove(cid);
            }
        }
        return infos;
    }

    public static List<ClientInfo> getAllClientInfos(){
        List<ClientInfo> infos = new ArrayList<ClientInfo>();
        for(ClientInfo info : clientInfoMap.values()){
            if(info.getData() == null ||  "CLIENT".equalsIgnoreCase((String) info.getData().get("client_type"))) {
               infos.add(info);
            }
        }
        return infos;
    }

    public static void register(ClientInfo info){
        clientInfoMap.put(info.getCid(), info);
    }

    public static void deregister(ClientInfo info){
        clientInfoMap.remove(info.getCid());
    }
}
