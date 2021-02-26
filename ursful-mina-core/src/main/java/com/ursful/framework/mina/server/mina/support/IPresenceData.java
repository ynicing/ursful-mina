package com.ursful.framework.mina.server.mina.support;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.server.mina.ClientInfo;

import java.util.Map;

/**
 * 类名：IPresence
 * 创建者：huangyonghua
 * 日期：2019/2/28 14:20
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IPresenceData  extends IOrder {
    ClientInfo presence(ClientInfo data);
}
