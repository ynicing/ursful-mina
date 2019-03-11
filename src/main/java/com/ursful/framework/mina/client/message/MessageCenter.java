package com.ursful.framework.mina.client.message;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 类名：MessageCenter
 * 创建者：huangyonghua
 * 日期：2019/3/4 17:46
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class MessageCenter {

    protected final Collection<MessageCollector> collectors = new ConcurrentLinkedQueue<MessageCollector>();

    private static MessageCenter center = new MessageCenter();

    public static MessageCenter getDefault(){
        return center;
    }

    private MessageCenter(){}

    public Collection<MessageCollector> getCollectors() {
        return collectors;
    }

    public static Collection<MessageCollector> getPacketCollectors() {
        return getDefault().getCollectors();
    }

    public MessageCollector create(String filterId) {
        MessageCollector collector = new MessageCollector(filterId);
        // Add the collector to the list of active collectors.
        collectors.add(collector);
        return collector;
    }

    public void remove(MessageCollector collector) {
        collectors.remove(collector);
    }

    public static MessageCollector createPacketCollector(String filterId) {
        return getDefault().create(filterId);
    }

    public static void removePacketCollector(MessageCollector collector) {
        getDefault().remove(collector);
    }
}
