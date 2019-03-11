package com.ursful.framework.mina.client.message;

import com.ursful.framework.mina.client.tools.ClientPacketCreator;
import com.ursful.framework.mina.common.packet.Packet;
import com.ursful.framework.mina.common.support.User;
import com.ursful.framework.mina.common.tools.StringUtils;

/**
 * 类名：Message
 * 创建者：huangyonghua
 * 日期：2019/3/4 17:43
 * 版权：Hymake Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Message {

    private static long cid = 0;

    private static String prefix = StringUtils.randomString(5) + "-";

    public static synchronized String nextID() {
        return prefix + Long.toString(cid++);
    }

    private String id;
    public String getId() {
        if(id == null){
            id = nextID();
        }
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String fromCid;
    private String toCid;
    private Object data;
    private Integer type = 0;

    public Message(){
        this.id = nextID();
    }

    public String getFromCid() {
        return fromCid;
    }

    public void setFromCid(String fromCid) {
        this.fromCid = fromCid;
    }

    public String getToCid() {
        return toCid;
    }

    public void setToCid(String toCid) {
        this.toCid = toCid;
    }

    public <T> T getData() {
        return (T)data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Message reply(Object data){
       return reply(data, this.toCid);
    }

    public Message reply(Object data, String fromCid){
        Message message = new Message();
        message.setId("reply-" + this.id);
        message.setFromCid(fromCid);
        message.setToCid(this.fromCid);
        message.setType(this.type);
        if(data != null) {
            message.setData(data);
        }
        return message;
    }

    public User  getFromUser(){
        return new User(getFromCid());
    }

    public User  getToUser(){
        return new User(getToCid());
    }

    public Packet getPacket(){
        return MessageCreator.createMessage(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", fromCid='" + fromCid + '\'' +
                ", toCid='" + toCid + '\'' +
                ", type=" + type +
                ", data=" + (data != null?data.toString():data) +
                '}';
    }
}
