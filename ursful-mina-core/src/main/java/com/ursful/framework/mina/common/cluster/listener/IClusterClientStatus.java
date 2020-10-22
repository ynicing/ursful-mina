package com.ursful.framework.mina.common.cluster.listener;

/**
 * 类名：IClientReady
 * 创建者：huangyonghua
 * 日期：2019/3/7 8:50
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IClusterClientStatus {
    void serverClientReady(String fromCid, String host, int port);
    void serverClientClose(String fromCid, String host, int port);
}
