package com.ursful.framework.mina.server;

/**
 * 类名：Server1
 * 创建者：huangyonghua
 * 日期：2019/2/25 14:08
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Server2 {
    public static void main(String[] args) throws Exception{

        UrsServer server = new UrsServer("server2", 9091);
//        server.setClusterIps("127.0.0.1:9091,127.0.0.1:9093");
        server.setClusterIps("127.0.0.1:9090");
        server.enableTransfer(true);
        server.run();
    }
}
