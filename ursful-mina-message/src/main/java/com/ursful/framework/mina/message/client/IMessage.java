package com.ursful.framework.mina.message.client;

import com.ursful.framework.mina.common.support.IOrder;
import com.ursful.framework.mina.message.support.Message;
import com.ursful.framework.mina.message.support.MessageSession;

/**
 * 类名：IPresence
 * 创建者：huangyonghua
 * 日期：2019/2/28 14:20
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IMessage extends IOrder {
    void message(Message message, MessageSession session);
}
