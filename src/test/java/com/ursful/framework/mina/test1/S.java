package com.ursful.framework.mina.test1;

import com.ursful.framework.mina.client.message.Message;
import com.ursful.framework.mina.common.InterfaceManager;
import com.ursful.framework.mina.server.UrsServer;
import com.ursful.framework.mina.server.client.Client;
import com.ursful.framework.mina.server.message.IServerMessage;
import com.ursful.framework.mina.server.mina.ClientInfo;
import com.ursful.framework.mina.server.mina.ClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：Server1
 * 创建者：huangyonghua
 * 日期：2019/2/25 14:08
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class S {

    public static void main(String[] args) throws Exception{

        UrsServer server = new UrsServer("server1", 9090);
        server.enableTransfer(false);
        new Thread(server).start();
    }
}
