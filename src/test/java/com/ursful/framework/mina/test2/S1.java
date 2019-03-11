package com.ursful.framework.mina.test2;


import com.ursful.framework.mina.server.UrsServer;

/**
 * 类名：Server1
 * 创建者：huangyonghua
 * 日期：2019/2/25 14:08
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class S1 {



    public static void main(String[] args) throws Exception{

        UrsServer server = new UrsServer("server1", 9090);
        server.setClusterIps("127.0.0.1:9091");
        server.enableTransfer(true);
        server.run();
    }
}
