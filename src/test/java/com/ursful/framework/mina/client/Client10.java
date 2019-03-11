package com.ursful.framework.mina.client;

import com.ursful.framework.mina.client.message.*;

import java.util.Map;
import com.ursful.framework.mina.common.InterfaceManager;
/**
 * 类名：Client1
 * 创建者：huangyonghua
 * 日期：2019/2/25 15:44
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Client10 {
    public static void main(String[] args) throws Exception{

        InterfaceManager.register(new IMessage() {
            @Override
            public void message(Message message, MessageSession session) {
                System.out.println(message.getFromCid() + ">" + message.getToCid() + ">" + message.getData());
            }
        });

        InterfaceManager.register(new IPresence() {
            @Override
            public void presence(String cid, boolean online,  Map<String, Object> data) {
                System.out.println("[" + cid + "]:" + (online?"Online":"Offline") + " >>> " + data);
            }
        });

        InterfaceManager.register(new IPresenceInfo() {
            @Override
            public void presences(Map<String, Map<String, Object>> cids) {
                System.out.println(cids);
            }
        });

        UrsClient client = new UrsClient("client10", "127.0.0.1", 9090);
        new Thread(client).run();
    }
}
