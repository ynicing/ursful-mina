package com.ursful.framework.mina.client.presence;

import com.ursful.framework.mina.common.support.ClientInfo;
import com.ursful.framework.mina.common.support.IOrder;

import java.util.List;

/**
 * 类名：IPresence
 * 创建者：huangyonghua
 * 日期：2019/2/28 14:20
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IPresenceInfo  extends IOrder {
    void presences(List<ClientInfo> clientInfos);
}
