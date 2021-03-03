package com.ursful.framework.mina.common.support;

import com.ursful.framework.mina.client.UrsClient;

/**
 * 类名：IClientReady
 * 创建者：huangyonghua
 * 日期：2019/3/7 8:50
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IClientStatus  extends IOrder{
    default void clientReady(UrsClient client, String cid){

    }
    default void clientClose(UrsClient client, String cid){

    }

    default void clientError(UrsClient client, int errorCode, String message){

    }

}
